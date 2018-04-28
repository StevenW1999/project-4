using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.SqlClient;
using System.Timers;
using System.Diagnostics;
using Mono.Data.Sqlite;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Android.Graphics;
using System.IO;
using Android_App.DatabaseModels;

/// <summary>
/// TODO
/// Add reference to timer event from the system class
/// Initiate this with checking passwords to eachother
/// </summary>
namespace Android_App
{
    [Activity(Label = "SignUpActivity", Theme = "@style/Theme.Custom")]
    public class SignUpActivity : Activity
    {
        private bool passwordsMatch = false;

        static TodoItemDatabase database;

        internal static TodoItemDatabase Database { get => database; set => database = value; }

        protected override void OnCreate(Bundle savedInstanceState)
        {

            base.OnCreate(savedInstanceState);
            // Create your application here
            SetContentView(Resource.Layout.SignUpPage);
            global::Xamarin.Forms.Forms.Init(this, savedInstanceState);
            Xamarin.Forms.DependencyService.Register<FileHelper>();
            //Define text entry fields
            TextView usernameField = FindViewById<TextView>(Resource.Id.UsernameField);
            TextView passwordField1 = FindViewById<TextView>(Resource.Id.FirstPasswordEntry);
            TextView passwordField2 = FindViewById<TextView>(Resource.Id.SecondPasswordEntry);
            //Check password combination when text field input changes
            passwordField2.TextChanged += delegate { CheckPasswordCombination(passwordField1, passwordField2); };
            //Check username availibility
            usernameField.TextChanged += delegate { /*CheckUsernameAvailability(usernameField); Vanwege lag uit gecomment*/ };
            //Add button action
            Button cancel = FindViewById<Button>(Resource.Id.Cancel);
            cancel.Click += delegate { CancelAction(); };

            //Add button Create account
            Button createAccount = FindViewById<Button>(Resource.Id.CreateAccount);
            createAccount.Click += delegate { CreateAccount(usernameField,passwordField1,passwordField2); };

        }

        private bool CheckUsernameAvailability(TextView usernameField)
        {
            if (new ExternalDB().UsernameAvailibility(usernameField.Text).Result == true)
            {
                return true;
            }
            else
            {
                usernameField.SetError("Username is not available",null);
                return false;
            }
        }

        private void CheckPasswordCombination(TextView passwordField1 , TextView passwordField2)
        {
            if(passwordField1.Text != passwordField2.Text)
            {
                passwordField2.SetError("Passwords dont match",null);
                this.passwordsMatch = false;
            }
            else
            {
                this.passwordsMatch = true;
            }
        }

        private void CancelAction()
        {
            //Close current activity
            Finish();
        }

        private void CreateAccount(TextView username, TextView password, TextView password2)
        {
            if (ExternalDB.TestConn().Result == true)
            {
                if ((password.Text == password2.Text) && CheckUsernameAvailability(username) == true)
                {

                    //Create user here
                    string usernameText = username.Text;
                    string passwordText = password.Text;
                    Toast.MakeText(this, "Creating user...", ToastLength.Short).Show();
                    new ExternalDB().AddUser(usernameText, passwordText);
                    Toast.MakeText(this, "Added a user", ToastLength.Short).Show();
                    Finish();
                }
                else if (password.Text != password2.Text)
                {
                    Toast.MakeText(this, "Passwords dont match", ToastLength.Short).Show();
                }
                else if (CheckUsernameAvailability(username) == false)
                {
                    Toast.MakeText(this, "Username is not available", ToastLength.Short).Show();
                }
            }
            else
            {
                Toast.MakeText(this, "No connection with the database", ToastLength.Short).Show();
            }
            /*
            Toast.MakeText(this, "Creating user...", ToastLength.Short).Show();

            string dbName = "AndroidAppDB.db";
            string folder = System.Environment.GetFolderPath(System.Environment.SpecialFolder.Personal);
            string dbPath = System.IO.Path.Combine(folder,dbName);
            //DOES FIND THE DATABASE IN ASSETS?
            //System.Diagnostics.Debug.WriteLine(Assets.List("")[0]);
            if (Database == null)
            {
                if (!File.Exists(dbPath))
                {
                    System.Diagnostics.Debug.WriteLine("Copying database");
                    //Kopieer database naar phone
                    using (var binaryReader = new BinaryReader(Android.App.Application.Context.Assets.Open(dbName)))
                    {
                        using (var binaryWriter = new BinaryWriter(new FileStream(dbPath, FileMode.Create)))
                        {
                            byte[] buffer = new byte[2048];
                            int length = 0;
                            while ((length = binaryReader.Read(buffer, 0, buffer.Length)) > 0)
                            {
                                binaryWriter.Write(buffer, 0, length);
                            }
                        }
                        //
                    }
                }
                else System.Diagnostics.Debug.WriteLine("database exist already");

                //New database file for external storage
                //var sqliteFilename = "MyDb.db";
                //var extStoragePath = global::Android.OS.Environment.ExternalStorageDirectory.AbsolutePath;
                //var path = System.IO.Path.Combine(extStoragePath, "");
                //var filename = System.IO.Path.Combine(extStoragePath, sqliteFilename);

                //System.Diagnostics.Debug.WriteLine(extStoragePath);
                // Check if we can write to external storage and copying the db file to external storage file
                //System.Diagnostics.Debug.WriteLine(Android.OS.Environment.ExternalStorageDirectory.CanWrite());
                //System.IO.File.Copy(dbPath, System.IO.Path.Combine(extStoragePath, sqliteFilename), true);
            }

            Database = new TodoItemDatabase(Xamarin.Forms.DependencyService.Get<IFileHelper>().GetLocalFilePath(dbPath));
            Database.SaveItemAsync(new User() { Username = $"Lol{new Random().NextDouble()}", Password = "pass" });
            List<User> users = Database.GetItemsNotDoneAsync().Result;
            System.Diagnostics.Debug.WriteLine(users.Count);

            Toast.MakeText(this, $"Total users: {users.Count}", ToastLength.Long).Show();

            //Commented code in scope here => to be used later
            {
                ////Functie to check username availibility
                //Func<string,bool> ValidateUsername = usernameGiven =>
                //{
                //    //using (SqlConnection connection = new SqlConnection(connectionString))
                //    //{
                //    //    connection.Open();
                //    //    // Do work here; connection closed on following line.
                //    //};
                //    return true;
                //};
                ////Check username
                //if(ValidateUsername(username.Text) == false)
                //{
                //    username.SetError("Username already exists", null);
                //}
                //else if(ValidateUsername(username.Text) == true && passwordsMatch == true)
                //{
                //    //TODO Call database to create a new user with specified parameters
                //    //CUSTOM MESSAGE BOX BUILDER!
                //    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                //    alert.SetTitle("Account created");
                //    alert.SetPositiveButton("Ok", (s,e)=> {
                //        Finish();
                //    });
                //    Dialog dialog = alert.Create();
                //    dialog.Show();
                //}
            }*/
        }
    }
}
