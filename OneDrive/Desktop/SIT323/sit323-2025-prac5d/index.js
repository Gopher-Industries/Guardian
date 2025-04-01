// index.js
const express = require('express');
const app = express();
const port = process.env.PORT || 3000;

app.get('/', (req, res) => {
  res.send('SIT323 Task 5.2D is running!');
});

app.listen(port, () => {
  console.log(`App listening on port ${port}`);
});
