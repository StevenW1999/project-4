using Android.App;
using Android.Widget;
using Android.OS;
using System.Diagnostics;
using System;
using System.Text;
using Android.Content;

namespace Android_App
{
    // Theme = "style/Theme.Name" to load custom theme.
    [Activity(Label = "Android_App", MainLauncher = true, Icon = "@mipmap/icon", Theme = "@style/Theme.Custom")]
    public class MainActivity : Activity
    {
        /// <summary>
        /// This function is called upon starting the program => inheritance
        /// </summary>
        /// <param name="savedInstanceState"></param>
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Set our view from the "main" layout resource and getting the information from this field.
            SetContentView(Resource.Layout.Main);
            
            // Get our button from the layout resourceand attach an event to it
            Button loginButton = FindViewById<Button>(Resource.Id.LoginButton);
            //Added a function to the click action of a the button
            loginButton.Click += delegate { loginAction(); };

            //Add a sign up button and define an action to be executed
            Button signUpButton = FindViewById<Button>(Resource.Id.SignUpButton);
            //Added a function to the click action of a the button
            signUpButton.Click += delegate { LoadSignUpPage(); };

        }

        private void LoadSignUpPage()
        {
            //Init new Activity page / new android form
            Intent signUp = new Intent(this,typeof(SignUpActivity));
            //Starting new activity page
            StartActivity(signUp);
        }

        /// <summary>
        /// This function is called after the login button is clicked
        /// Contains getting information from predefined fields in the AXML file.
        /// </summary>
        private void loginAction()
        {
            //TEST DB CONN HERE
            if(ExternalDB.TestConn() == true)
            {
                //USUAL CODE UNDER HERE!
                EditText usernameField = FindViewById<EditText>(Resource.Id.UsernameField);
                string username = usernameField.Text;
                EditText passwordField = FindViewById<EditText>(Resource.Id.PasswordField);
                string password = passwordField.Text;
                System.Diagnostics.Debug.WriteLine($"Given Username : {username} and password : {password}");
                //Validate user with online DB
                if (ExternalDB.ValidateLogin(username, password) == true)
                {
                    Toast.MakeText(this, "Login Succesfull", ToastLength.Short).Show();
                }
                else
                {
                    //ERROR ICON HAS TO BE SET STILL
                    Android.Graphics.Drawables.Drawable icon = null;
                    usernameField.SetError("Wrong Username", icon);
                    passwordField.SetError("Wrong Password", icon);
                    Toast.MakeText(this, "Wrong Username or Password", ToastLength.Short).Show();
                    //CUSTOM MESSAGE BOX BUILDER!
                    {
                        //AlertDialog.Builder alert = new AlertDialog.Builder(this);
                        //alert.SetTitle("Error");
                        //alert.SetMessage("Wrong username and/or password");
                        //alert.SetPositiveButton("Ok", handler: null);
                        //Dialog dialog = alert.Create();
                        //dialog.Show();
                    }
                }
            }
            //IF THERE IS NO DATABASE CONNECTION
            else
            {
                Toast.MakeText(this, "No database connection established", ToastLength.Short).Show();
            }
        }
    }
}

