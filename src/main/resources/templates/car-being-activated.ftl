<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Announcement Activation Notification</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            color: #333;
            text-align: center;
            padding: 20px;
        }
        .container {
            background: #ffffff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            display: inline-block;
            max-width: 500px;
        }
        .header {
            color: #007bff;
        }
        .success {
            color: darkgreen;
            font-weight: bold;
        }
        .warning {
            color: darkred;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1 class="header">Your Announcement Has Been Activated</h1>
        <p>Hello, <strong>${name}</strong>,</p>
        <p>We are happy to inform you that your announcement has been successfully activated on AutoRia.</p>
        
        <p class="success">No offensive language was detected, and your listing is now live.</p>

        <p><strong>Car ID:</strong> ${car_id}</p>

        <p>Thank you for using AutoRia! We wish you the best of luck with your sales.</p>
        
        <p>With respect,</p>
        <p><strong>AutoRia Team</strong></p>
    </div>
</body>
</html>
