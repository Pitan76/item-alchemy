#!/usr/local/bin/php -q
<?php

$java = file_get_contents("input.java");

//$ptn = "/public\sstatic\sfinal\sItemGroup\s([a-zA-Z0-9_-]+)\s=\snew\sItemGroup/";
$ptn = "/public\sstatic\sfinal\sMapColor\s([a-zA-Z0-9_-]+)\s=\snew\sMapColor/";
//$ptn = "/public\sstatic\sfinal\sMaterial\s([a-zA-Z0-9_-]+)\s=\snew\sBuilder/";
preg_match_all($ptn, $java, $m);
$data = "";
foreach($m[1] as $s) {
	$data .= "public static final BaseMaterialColor " . $s . " = new BaseMaterialColor(MapColor." . $s . ");\n";
//	$data .= "public static final CreativeTab " . $s . " = new CreativeTab(ItemGroup." . $s . ");\n";
//	$data .= "case \"" . $s . "\":\nreturn " . $s . ";\n";
}
file_put_contents("output.txt", $data);