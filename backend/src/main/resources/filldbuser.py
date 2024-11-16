import mysql.connector
from faker import Faker

# Connect to MySQL database
connection = mysql.connector.connect(
    host="localhost",
    user="my_user",
    password="handsonhcidb",
    database="my_database"
)
cursor = connection.cursor()
faker = Faker()

# Insert 150 users with random names, passwords, and profile pictures
for _ in range(150):
    first_name = faker.first_name()
    last_name = faker.last_name()
    email = faker.email()
    password = faker.password()  # Generate a random password
    profile_photo = faker.image_url()  # Generate a random profile photo URL
    
    sql = """
    INSERT INTO users (first_name, last_name, email, password, account_verified, email_verified, enabled, role, report_exp, profile_photo)
    VALUES (%s, %s, %s, %s, b'1', b'1', b'1', 'user', 1, %s)
    """
    cursor.execute(sql, (first_name, last_name, email, password, profile_photo))

# Commit the transactions
connection.commit()

# Fetch the user IDs
cursor.execute("SELECT id FROM users ORDER BY id DESC LIMIT 150")
user_ids = [row[0] for row in cursor.fetchall()]

# Insert follow relationships
for follower_id in user_ids:
    for followed_id in user_ids:
        if follower_id != followed_id:
            sql = "INSERT INTO follow_users (follower_id, followed_id) VALUES (%s, %s)"
            cursor.execute(sql, (follower_id, followed_id))

# Commit the follow transactions
connection.commit()

# Close the connection
cursor.close()
connection.close()
