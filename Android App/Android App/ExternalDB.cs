﻿using System;
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
using System.Configuration;
using System.Threading.Tasks;

/// <summary>
/// THIS IS A TEST OBJECT TO ESTABLISH CONNECTION TO THE DATABASE
/// </summary>
namespace Android_App
{
    public class ExternalDB
    {
        private static string connString = "Server = hogeschoolrotterdam.database.windows.net,1433; Database = ASS; User Id = main; Password = database1#;";//ConfigurationManager.ConnectionStrings["ASS"].ConnectionString;
        private static SqlConnection conn;
        public ExternalDB()
        {
            //To initialize External db object
        }

        //THIS FUNCTION IS A TEST TO RECIEVE INFORMATION
        public static Task<bool> TestConn()
        {
            return Task.Run(() =>
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
            });
        }

        public Task<bool> UsernameAvailibility(string username)
        {
            return Task<bool>.Factory.StartNew(() =>
            {
                bool usernameAvailible = true;
                try
                {
                    using (conn = new SqlConnection(connString))
                    {
                        conn.Open();

                        SqlCommand command = new SqlCommand
                        {
                            CommandText = $"select * from users where Username = '{username}'",
                            Connection = conn
                        };

                        SqlDataReader reader = command.ExecuteReader();
                        if (reader.HasRows == true)
                        {
                            usernameAvailible = false;
                        }
                        conn.Close();
                    }
                }
                catch
                {
                    System.Diagnostics.Debug.WriteLine("Server could not be reached");
                }
                return usernameAvailible;
            });
        }

        //To validate user credentials
        public Task<bool> ValidateLogin(string username, string password)
        {
            return Task<bool>.Factory.StartNew(() =>
            {
                bool userValidated = false;
                try
                {
                    using (conn = new SqlConnection(connString))
                    {
                        conn.Open();

                        SqlCommand command = new SqlCommand
                        {
                            CommandText = $"select * from users where Username = '{username}' and Password = '{password}'",
                            Connection = conn
                        };

                        SqlDataReader reader = command.ExecuteReader();
                        if (reader.HasRows == true)
                        {
                            userValidated = true;
                        }
                        conn.Close();
                    }
                }
                catch
                {
                    System.Diagnostics.Debug.WriteLine("Failed to validate login");
                }

                return userValidated;
            });
        }

<<<<<<< HEAD
        public Task AddUser(string username , string password)
        {
            return Task.Factory.StartNew(() => {
                try
                {
                    using (conn = new SqlConnection(connString))
                    {
                        conn.Open();

                        SqlCommand command = new SqlCommand
                        {
                            CommandText = $"insert into users values ('{username}' , '{password}')",
                            Connection = conn
                        };

                        command.ExecuteNonQuery();
                        conn.Close();
                    }
                }
                catch
                {
                    System.Diagnostics.Debug.WriteLine("Failed to Add a user to the database");
                }
            });
        }
=======
>>>>>>> 6a1253009dcdaefb11d04648629143bc7366d9c7
    }
}