<?php
$modid = 'itemalchemy';
foreach (glob('../models/item/*') as $file) {
	$id = preg_replace('/(.*)\.json/', "$1", basename($file));
	file_put_contents($id . '.json', <<<EOD
{
  "model": {
    "type": "minecraft:model",
    "model": "{$modid}:item/{$id}"
  }
}
EOD);
}
