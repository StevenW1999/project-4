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
using System.Data.SqlClient;

/// <summary>
/// THIS IS A TEST OBJECT TO ESTABLISH CONNECTION TO THE DATABASE
/// </summary>
namespace Android_App
{
    public static class ExternalDB
    {
        private static string connString = "Server = hogeschoolrotterdam.database.windows.net,1433; Database = ASS; User Id = main; Password = database1#;Connection Timeout = 1;";
        private static SqlConnection conn;

        //THIS FUNCTION IS A TEST TO RECIEVE INFORMATION
        public static bool TestConn()
        {
            try
            {
                using (conn = new SqlConnection(connString))
                {
                    conn.Open();

                    SqlCommand command = new SqlCommand
                    {
                        CommandText = "select * from users",
                        Connection = conn
                    };

                    SqlDataReader reader = command.ExecuteReader();
                    while (reader.Read())
                    {
                        System.Diagnostics.Debug.WriteLine(reader["Username"].ToString(), reader["Password"].ToString());
                    }
                    conn.Close();
                }
                return true;

            }
            catch
            {
                return false;
            }
        }

        public static bool ValidateLogin(string username , string password)
        {
            bool userValidated = false;
            using (conn = new SqlConnection(connString))
            {
                conn.Open();

                SqlCommand command = new SqlCommand
                {
                    CommandText = $"select * from users where Username = '{username}' and Password = '{password}'",
                    Connection = conn
                };

                SqlDataReader reader = command.ExecuteReader();
                if(reader.HasRows == true)
                {
                    userValidated = true;
                }
                conn.Close();
            }
            return userValidated;
        }
    }
}