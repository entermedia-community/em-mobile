$(document).ready(function(){/* off-canvas sidebar toggle */

$('[data-toggle=offcanvas]').click(function() {
  	//$(this).toggleClass('visible-xs text-center');
	 var url = $(this).data("urlstatesave");
    $(this).find('i').toggleClass('press-up press-down');
   $(".sidebar-offcanvas").toggle();
   
   $('#main').toggleClass('col-sm-10 col-sm-12').toggleClass('col-xs-11 col-xs-12');
   
   jQuery.get(url, {}, function(data) 
	{
		//cell.html(data);
	   doResize();
	});
    //$('.row-offcanvas').toggleClass('active');
    
   // $('#lg-menu').toggleClass('visible-xs').toggleClass('hidden-xs');
   // $('#xs-menu').toggleClass('hidden-xs').toggleClass('visible-xs');
    //$('#btnShow').toggle();
});

jQuery('.playerclink').bind('click',function(e)
{
	e.preventDefault();
	var link = $(this);

	var hidden = $("#hiddenoverlay");

	loadInto(link.attr("href") + "&oemaxlevel=1",hidden);

	//Now show overlay
	hidden.show();
	
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

	$(document).on('click',"#closebutton",function(e)
	{	
		e.preventDefault();
		var hidden = $("#hiddenoverlay");
		hidden.hide();
	});
	
	$(document).on('click',"#playbutton",function(e)
	{	
		e.preventDefault();
		var div = $(this);
		div.removeClass("glyphicon-play");
		div.addClass("glyphicon-pause");
		console.log("Now Play slideshow");
	});

	doResize();
});


$(window).resize(function(){
	doResize();
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
			if( isNaN(w) )
			{
				w = 160;
			}
		}
		
		if( useimage )
		{
			h= cell.data("height");
			if(isNaN(h) )
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
	