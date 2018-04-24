using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

namespace Android_App
{
    [Activity(Label = "SignUpActivity", Theme = "@style/Theme.Custom")]
    public class SignUpActivity : Activity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            // Create your application here
            SetContentView(Resource.Layout.SignUpPage);

            //Add button action
            Button cancel = FindViewById<Button>(Resource.Id.Cancel);
            cancel.Click += delegate { CancelAction(); };
        }

        private void CancelAction()
        {
            //Close current activity
            Finish();
        }
    }
}