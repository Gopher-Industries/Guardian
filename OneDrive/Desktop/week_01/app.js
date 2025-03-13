const express = require('express');
const app = express();
const port = 3000;

//Basic route

app.get('/', (req, res) => {
    res.send('Hello World!');
});

//Start teh server

app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`);
});
