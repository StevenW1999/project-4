using Android.App;
using Android.Widget;
using Android.OS;
using System.Diagnostics;
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

            // Get our button from the layout resource,and attach an event to it
            Button loginButton = FindViewById<Button>(Resource.Id.LoginButton);
            //Added a function to the click action of a the button
            loginButton.Click += delegate { loginAction(); };
        }

        /// <summary>
        /// This function is called after the login button is clicked
        /// Contains getting information from predefined fields in the AXML file.
        /// </summary>
        private void loginAction()
        {
            EditText usernameField = FindViewById<EditText>(Resource.Id.UsernameField);
            string username = usernameField.Text;
            EditText passwordField = FindViewById<EditText>(Resource.Id.PasswordField);
            string password = passwordField.Text;
            System.Diagnostics.Debug.WriteLine($"Given Username : {username} and password : {password}");
        }
    }
}

