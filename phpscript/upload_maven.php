<?php
$gradle_properties = file_get_contents('../gradle.properties');
preg_match('/mod_version=(.*)/', $gradle_properties, $matches);
define('VERSION', $matches[1]);

define('GROUP_ID', 'net.pitan76');
define('ARTIFACT_ID', 'itemalchemy');

define('DIRS', array(
	'fabric' => 'build/',
));

foreach (DIRS as $type => $dir) {
	$postData = array();
	
	$postData['group_id'] = GROUP_ID;
	$postData['artifact_id'] = ARTIFACT_ID . '-' . $type;
	$postData['version'] = VERSION;
	
	$files = array(
		$dir . 'libs/' . ARTIFACT_ID . '-' . VERSION . '.jar',
		$dir . 'libs/' . ARTIFACT_ID . '-' . VERSION . '-sources.jar',
		$dir . 'publications/maven' .  ucfirst($type) . '/' . ARTIFACT_ID . '.pom',
	);
	
	$pom = '../' . $dir . 'publications/maven' .  ucfirst($type) . '/' . ARTIFACT_ID . '.pom';
	
	$pom_str = file_get_contents($pom);
	//$pom_str = preg_replace('/' . preg_quote(VERSION . PLATFORM_FILE_MARK[$type] . '</version>', '/'). '/', VERSION . '</version>', $pom_str, 1);
	//$pom_str = preg_replace('/' . preg_quote(ARTIFACT_ID . PLATFORM_FILE_MARK[$type]  . '</artifactId>', '/') . '/', ARTIFACT_ID .  ($type == "common" ? '-common' : PLATFORM_FILE_MARK[$type]) . '</artifactId>', $pom_str, 1);
	
	file_put_contents($pom, $pom_str);
	
	foreach ($files as $index => $file) {
		$postData['upload[' . $index . ']'] = curl_file_create(
			realpath("../" . $file),
			mime_content_type("../" . $file),
			basename("../" . $file)
		);
		echo "Uploading '" . $file . "'\n";
	}
	
	$request = curl_init('http://localhost/maven/maven.php');
	curl_setopt($request, CURLOPT_POST, true);
	curl_setopt($request, CURLOPT_POSTFIELDS, $postData);
	curl_setopt($request, CURLOPT_RETURNTRANSFER, true);
	$result = curl_exec($request);
	
	if ($result === false) {
		error_log(curl_error($request));
	}
	curl_close($request);
}
