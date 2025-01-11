import mysql.connector
import random

# Database connection
connection = mysql.connector.connect(
    host="localhost",
    user="root",
    password="handsonhcidb",
    database="my_database"
)

# Create a cursor
cursor = connection.cursor()

# Define the list of profile photo URLs
# elles sont dans src/main/resources/images
profile_photos = [
    "src/main/resources/images/1.jpg",
    "src/main/resources/images/2.jpg",
    "src/main/resources/images/3.jpg",
    "src/main/resources/images/4.jpg",
    "src/main/resources/images/5.jpg",
    "src/main/resources/images/6.jpg",
    "src/main/resources/images/7.jpg",
    "src/main/resources/images/8.jpg",
   
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
