<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Announcement Banned Notification</title>
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
            color: #d9534f;
        }
        .desc {
            background-color: #282c34;
            color: aliceblue;
            padding: 10px;
            border-radius: 5px;
            font-family: 'Courier New', Courier, monospace;
            word-wrap: break-word;
        }
        .warning {
            color: darkred;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1 class="header">Announcement Banned</h1>
        <p>Hello, <strong>${name}</strong>,</p>
        <p class="warning">Our platform strives to maintain a respectful environment. Our management team has detected offensive content in your announcement.</p>

        <p>We regret to inform you that your announcement has been permanently banned from the platform due to violation of our guidelines.</p>

        <p>If you believe this decision was made in error or would like further clarification, please feel free to contact our support team.</p>

        <p>With respect,</p>
        <p><strong>AutoRia Team</strong></p>
    </div>
</body>
</html>
