$(document).ready(function(){/* off-canvas sidebar toggle */

$('[data-toggle=offcanvas]').click(function() {
	 var url = $(this).data("urlstatesave");
    $(this).find('i').toggleClass('press-up press-down');
   $(".sidebar-offcanvas").toggle();
   
   $('#main').toggleClass('col-sm-10 col-sm-12').toggleClass('col-xs-11 col-xs-12');
   
   jQuery.get(url, {}, function(data) 
	{
	   doResize();
	});
});

jQuery('.playerclink').bind('click',function(e)
{
	e.preventDefault();
	var link = $(this);
	var image = $('img', link);
	if (image.length) {
		console.log(image);
		var percentleft = Math.floor(((e.pageX - link.offset().left) / image.width()) * 100);
		var percenttop = Math.floor(((e.pageY - link.offset().top) / image.height()) * 100);
	
		if (percenttop >= 70) {
			console.log('Click in bottom 30%');
			return;
		}
	}
	
	var href = link.attr("href");
	if (href.indexOf("?") === -1) {
		href = href + "?";
	} else {
	    href = href + "&";
	}
	var hidden = $("#hiddenoverlay");

	loadInto(href + "oemaxlevel=1",hidden);

	//Now show overlay
	hidden.show();

});

jQuery('a.imageplayer').on('click',function(e)
{
	e.preventDefault();
	var link = $(this);
	var image = $('img', link);
	var percentleft = Math.floor(((e.pageX - link.offset().left) / image.width()) * 100);
	var percenttop = Math.floor(((e.pageY - link.offset().top) / image.height()) * 100);

	if (percentleft >= 70) {
		console.log('Click on right 30%');
	} else if (percentleft <= 30) {
		console.log('Click on left 30%');
	}
});

	jQuery('.addfilter').bind('click',function(e)
	{	
		e.preventDefault();
		//TODO: Why not update the results with new data like VD does?
			var link = $(this);
			var picked = link.data("filtertype");
			jQuery('#filtertype').val(picked);
			var value = link.data("filtervalue");
			jQuery('#filtervalue').val(value);
			var label = link.data("filterlabel");
			jQuery('#filterlabel').val(label);

			jQuery('#dontshow').submit();
			
	});

	$(document).on('click',".overlay-close",function(e)
	{	
		e.preventDefault();
		var hidden = $("#hiddenoverlay");
		hidden.hide();
	});
	
	$(document).on('click',".overlay-play",function(e)
	{	
		e.preventDefault();
		var div = $('span', this);
		div.removeClass("glyphicon-play");
		div.addClass("glyphicon-pause");
		console.log("Now Play slideshow");
	});

	$('.showmore').on('click', function(e) {
		e.preventDefault();	
		var element = $(this);
		if (element.hasClass('collapsed')) {
			element.text('Show Less');
		} else {
			element.text('Show More');
		}
	});	

	doResize();
});


$(window).resize(function(){
	doResize();
});

$(window).scroll(function() 
{
	var appid = $("body").data("appid");
	//are we near the end? Are there more pages?
   if($(window).scrollTop() + $(window).height() > $(document).height() + 100) 
   {
       return;
   }
   
   jQuery.get("/" + appid + "/components/results/gallery.html", {}, function(data) 
				{
					//cell.html(data);
				   doResize();
				});
});




loadInto = function(inLink,cell)
{
	jQuery.get(inLink, {}, function(data) 
	{
		cell.html(data);
	});
}


doResize = function() 
{
	var fixedheight = 200;
	var cellpadding = 16;
	var sofarused = 0;
	var totalwidth = 0;
	var rownum = 0;
	
	var totalavailable;
	if( $("#sidebar").is(":visible") )
	{
		totalavailable = 10/12 * $(window).width();
	}
	else
	{
		totalavailable = $(window).width(); //$(".masonry-grid").width() - 25;	Can't do this since it changes float while loading	
	}
	totalavailable  = totalavailable  - 5;//buffer
	console.log(totalavailable);
	var row = [];
	$(".masonry-grid .masonry-grid-cell").each(function()
	{		
		var cell = $(this);
		//var w = cell.data("width");
		var useimage = false;
		var w = jQuery("#emthumbholder img",cell).width();
		if(w == 0) //not loaded yet
		{
			useimage = true;
			w = cell.data("width");
			if( isNaN(w) || w == "" )
			{
				w = 160;
			}
		}
		
		if( useimage )
		{
			h= cell.data("height");
			if(isNaN(h)  || h == "")
			{
				h = 160;
			}			
		}
		else
		{
			h = jQuery("#emthumbholder img",cell).height();
		}
		w = parseInt(w);
		h = parseInt(h);
		var a = w / h;  
	
		//var hratio = h / fixedheight;  
		var neww = Math.floor( fixedheight * a );
		
		var over = sofarused + neww;
		if( over > totalavailable )
		{
			var overage = (totalavailable - row.length * cellpadding)/ sofarused;
			var newheight = fixedheight * overage;

			//Need to figure aspect of entire row
			var roundedheight = Math.floor( newheight ); //make smaller
			$.each( row, function()
				{
					var newcell = this;
					var newwidth = Math.floor(newheight * newcell.aspect); 
					
					//jQuery("#emthumbholder img",newcell.cell).height(roundedheight); //TODO: Fix aspect
					jQuery("#emthumbholder img",newcell.cell).width(newwidth);
					//newcell.cell.width(newwidth); //TODO: Fix aspect
					jQuery(".imagearea",newcell.cell).height(roundedheight); //TODO: Fix aspect
				}	
			);
			row = [];
			sofarused = 0;
			rownum = rownum + 1;
		}
		
		sofarused = sofarused + neww;
		row.push( {cell:$(cell), aspect:a, width:w, height:h} );		
		
	});
	
	//TODO: Move to method call
	var overage = (totalavailable - row.length * cellpadding)/ sofarused;
	var newheight = fixedheight * overage;
	if( newheight > fixedheight + 100)
	{
		newheight = fixedheight + 100
	}
	var roundedheight = Math.floor(newheight);
	$.each( row, function()
		{
			var newcell = this;
			var newwidth = Math.floor(newheight * newcell.aspect); 
			jQuery("#emthumbholder img",newcell.cell).width(newwidth);
			//newcell.cell.width(newwidth); //TODO: Fix aspect
			jQuery(".imagearea",newcell.cell).height(roundedheight); //TODO: Fix aspect
			//jQuery("#emthumbholder img",newcell.cell).width(newwidth);
		}	
	);
}
	