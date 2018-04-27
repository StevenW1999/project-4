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
            usernameField.TextChanged += delegate { CheckUsernameAvailibility(usernameField); };
            //Add button action
            Button cancel = FindViewById<Button>(Resource.Id.Cancel);
            cancel.Click += delegate { CancelAction(); };

            //Add button Create account
            Button createAccount = FindViewById<Button>(Resource.Id.CreateAccount);
            createAccount.Click += delegate { CreateAccount(usernameField,passwordField1,passwordField2); };

        }

        private void CheckUsernameAvailibility(TextView usernameField)
        {

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
            string dbName = "AndroidAppDB.db";
            string folder = System.Environment.GetFolderPath(System.Environment.SpecialFolder.Personal);
            string dbPath = System.IO.Path.Combine(folder,dbName);

            if (database == null)
            {
                database = new TodoItemDatabase(Xamarin.Forms.DependencyService.Get<IFileHelper>().GetLocalFilePath("AndroidAppDB.db"));
                database.SaveItemAsync(new User() {Username = "Lol" , Password = "pass" });
                List<User> users = database.GetItemsNotDoneAsync().Result;
                System.Diagnostics.Debug.WriteLine(users[0].Username);
            }
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
        }
    }
}