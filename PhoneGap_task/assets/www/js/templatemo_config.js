/* Credit: www.templatemo.com */
/*jquery config*/
$(function(){
				
							/*google map config*/
							


				//declare scroll animate function
				$.fn.anchorAnimate = function(settings) {
						settings = jQuery.extend({
								speed : 1100
						}, settings);	
						return this.each(function(){
								var caller = this
								$(caller).click(function (event) {	
										event.preventDefault();
										var elementClick = $(caller).attr("href") ;
										var destination = ( $(elementClick).index() * 415 ) ;
										$("#mid_container_wap:not(:animated)").animate({ scrollTop: destination}, settings.speed);
										return false;
								});
						})
				}
				//call scroll animate function
				$("a.anchor_animate").anchorAnimate();
				
				// create responsive function
				$.fn.avocadoAllignment = function(){
						setHeight = $(window).height() ;
						if(setHeight>(395 + 140)){
								$("#banner img").css( "marginTop" , (((setHeight-395) / 2) - 70 ) /  2 );
								$("#banner form").css( "marginTop" , (((setHeight-395) / 2)- 10 ) /  2 );
								$("#footer > div").css( "marginTop" , (((setHeight-395) / 2)- 25 ) /  2 );
								$("#banner").height( (setHeight-395) / 2 );
								$("#footer").height( (setHeight-395-140) / 2 );
						}else{
								$("#footer").height( 25 );
								$("#banner").height( 70 );
								$("#banner img").css( "marginTop" ,  0  );
								$("#banner form").css( "marginTop" ,  30  );
								$("#footer > div").css( "marginTop" , 0 );
						}
				}
				
				//call responsive function on window start
				$(document).avocadoAllignment();
				
				//call responsive function on window resize
				$(window).resize(function(){
						$(document).avocadoAllignment();
				});
				
});


