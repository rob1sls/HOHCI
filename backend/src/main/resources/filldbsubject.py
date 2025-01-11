import mysql.connector
from faker import Faker

# Connect to MySQL database
connection = mysql.connector.connect(
    host="localhost",
    user="root",
    password="handsonhcidb",
    database="my_database"
)
cursor = connection.cursor()
faker = Faker()

# Insert 150 users with random names, passwords, and profile pictures


 # Create a 1000 of user with the same password and email
for i in range(200):
    first_name = "John"
    last_name = "Doe"
    email = "johndoe" + str(i) + "@gmail.com"  # Generate a random email
    password = "Motdepasse!10"  # Generate a random password
    profile_photo = faker.image_url()  # Generate a random profile photo URL


    sql = """
    INSERT INTO users (first_name, last_name, email, password, account_verified, email_verified, enabled, role, report_exp, alreadyused)
    VALUES (%s, %s, %s, %s, b'1', b'1', b'1', 'user', 0, 0)
       """
    cursor.execute(sql, (first_name, last_name, email, password))
# Commit the transactions
connection.commit()

# Close the connection
cursor.close()
connection.close()
