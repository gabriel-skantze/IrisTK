<!DOCTYPE html>
<html class="no-js" lang="en" > 

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width">
  <title>IrisTK</title>
  <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,700' rel='stylesheet' type='text/css'>
  <link rel="stylesheet" href="css/foundation.css">
  <link rel="stylesheet" href="css/pandoc.css">
  <script src="js/vendor/custom.modernizr.js"></script>
  <script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
	
	  ga('create', 'UA-121669-8', 'iristk.net');
	  ga('send', 'pageview');
	
	</script>
</head>
<body>

	<div class="row">
		<div class="large-12 columns">
			<h1><a href="index.php">IrisTK</a></h1>
			<p class="tagline">Java-based dialogue system framework</p>
			<hr/>
		</div>
	</div>

	<div class="row">
	
		<div class="large-8 columns">
			
		<?php
		
		$page = $_GET["page"];
		
		if ($page == "") {
			$page = "start";
		}
		if(file_exists($page . ".html"))
			include($page . ".html");	
		
		?>
			
		</div>

		<div class="large-4 columns">
			<p><a href="index.php?page=download" class="button expand radius" style="margin-bottom: 0;">Download IrisTK</a></p>
			
			<h3>Guide to IrisTK</h3>
			
			<?php
			$groups = array(
				"Getting started" => array(
					"installation" => "Installation",
					"command-line_tool" => "Command-line tool",
					"develop_in_eclipse" => "Develop in Eclipse",
					"tutorial" => "Tutorial",
				),
				"System, modules and events" => array(
					"system_overview" => "System and Events",
					"events" => "Events",
					"creating_new_modules" => "Creating new modules",
					"distributed_systems" => "Distributed systems",
				),
				"IrisFlow" => array(
					"irisflow_overview" => "IrisFlow overview",
					"compiling_the_flow" => "Compiling the flow",
					"irisflow_reference" => "IrisFlow reference",
					"syntactic_sugar" => "Syntactic sugar and caveats",
				),
				"Addons and examples" => array(
					"microsoft" => "Addon: Microsoft Speech and Kinect",
					"embr" => "Addon: EMBR animated agent",
					"cereproc" => "Addon: Cereproc TTS",
					"nuance9" => "Addon: Nuance 9 ASR",
				),
				"Reference" => array(
					"javadoc/index.html" => "Javadoc",
				),
			);
			
			?><div class="section-container accordion" data-section="accordion"><?php
			foreach ($groups as $gname => $sub) {
				$gactive = "";
				foreach ($sub as $pname => $text) {
					if ($page == $pname) {
						$gactive = " class=\"active\"";
					}
				}
			?>
			  <section<?php echo $gactive?>>
				<p class="title" data-section-title><a href="#"><?php echo $gname?></a></p>
				<div class="content" data-section-content>
				    <ul class="side-nav"><?php
					foreach ($sub as $pname => $text) {
						if (strpos($pname,'.') !== false) {
							$href = $pname;
						} else {
							$href = "index.php?page=" . $pname;
						}
						?><li<?php if ($page == $pname) echo " class=\"active\""?>><a href="<?php echo $href?>"><?php echo $text?></a></li><?php
					}?>
					</ul>
				</div>
			  </section><?php
			}?>
			</div>
			
		</div>
	</div>
	
	<div class="row">
		<div class="large-12 columns">
			<hr/>
			<p>Copyright &copy; Gabriel Skantze, 2013-</p>
		</div>
	</div>

  <script>
  document.write('<script src=' +
  ('__proto__' in {} ? 'js/vendor/zepto' : 'js/vendor/jquery') +
  '.js><\/script>')
  </script>
  
  <script src="js/foundation.min.js"></script>

  <script src="js/foundation/foundation.js"></script>
  
  <script src="js/foundation/foundation.alerts.js"></script>
  
  <script src="js/foundation/foundation.clearing.js"></script>
  
  <script src="js/foundation/foundation.cookie.js"></script>
  
  <script src="js/foundation/foundation.dropdown.js"></script>
  
  <script src="js/foundation/foundation.forms.js"></script>
  
  <script src="js/foundation/foundation.joyride.js"></script>
  
  <script src="js/foundation/foundation.magellan.js"></script>
  
  <script src="js/foundation/foundation.orbit.js"></script>
  
  <script src="js/foundation/foundation.reveal.js"></script>
  
  <script src="js/foundation/foundation.section.js"></script>
  
  <script src="js/foundation/foundation.tooltips.js"></script>
  
  <script src="js/foundation/foundation.topbar.js"></script>
  
  <script src="js/foundation/foundation.interchange.js"></script>
  
  <script src="js/foundation/foundation.placeholder.js"></script>
  
  <script src="js/foundation/foundation.abide.js"></script>

  
  <script>
    $(document).foundation();
  </script>
</body>
</html>