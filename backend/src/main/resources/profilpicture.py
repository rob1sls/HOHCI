import mysql.connector
import random

# Database connection
connection = mysql.connector.connect(
    host="localhost",
    user="my_user",
    password="handsonhcidb",
    database="my_database"
)

# Create a cursor
cursor = connection.cursor()

# Define the list of profile photo URLs
profile_photos = [
    "http://localhost:4200/assets/images/avatar.jpg",
    "http://localhost:4200/assets/images/0.jpg",
    "http://localhost:4200/assets/images/1.jpg",
    "http://localhost:4200/assets/images/2.jpg",
    "http://localhost:4200/assets/images/3.jpg",
    "http://localhost:4200/assets/images/4.jpg",
    "http://localhost:4200/assets/images/5.jpg",
    "http://localhost:4200/assets/images/6.jpg",
    "http://localhost:4200/assets/images/7.jpg",
    "http://localhost:4200/assets/images/8.jpg"
]

try:
    # Select all users
    cursor.execute("SELECT id FROM users")
    users = cursor.fetchall()

    # Loop through each user and assign a random profile photo
    for user in users:
        user_id = user[0]
        random_photo = random.choice(profile_photos)
        
        # Update the user's profile photo
        cursor.execute("UPDATE users SET profile_photo = %s WHERE id = %s", (random_photo, user_id))

    # Commit the changes to the database
    connection.commit()
    print("Profile photos updated successfully!")

except mysql.connector.Error as err:
    print(f"Error: {err}")
    connection.rollback()

finally:
    # Close the cursor and connection
    cursor.close()
    connection.close()
