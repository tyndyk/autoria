<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Announcement Review Required</title>
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
        .warning {
            color: darkred;
            font-weight: bold;
        }
        .desc {
            background-color: #282c34;
            color: aliceblue;
            padding: 10px;
            border-radius: 5px;
            font-family: 'Courier New', Courier, monospace;
            word-wrap: break-word;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1 class="header">New Announcement Has Been Suspected</h1>
        <p class="warning">This announcement has been flagged as it did not pass our profanity filter.</p>

        <p>Offensive language was detected in the following text:</p>
        <p class="desc">${description}</p>

        <p>Please review the announcement and take appropriate action.</p>

        <p>Thank you for your attention to this matter.</p>

        <p>With respect,</p>
        <p><strong>AutoRia Team</strong></p>
    </div>
</body>
</html>
