using Android.App;
using Android.Widget;
using Android.OS;

namespace Android_App
{
    // "Theme = "@android:style/Theme.NoTitleBar"" added to remove title bar on top of application
    [Activity(Label = "Android_App", MainLauncher = true, Icon = "@mipmap/icon" , Theme = "@android:style/Theme.NoTitleBar")]
    public class MainActivity : Activity
    {
        int count = 1;
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.Main);

            // Get our button from the layout resource,
            // and attach an event to it
            Button button = FindViewById<Button>(Resource.Id.myButton);

            button.Click += delegate { button.Text = string.Format("{0} clicks!", count++); };
        }
    }
}

