
$(document).ready(function(){/* off-canvas sidebar toggle */

$('[data-toggle=offcanvas]').click(function() {
  	$(this).toggleClass('visible-xs text-center');
    $(this).find('i').toggleClass('glyphicon-chevron-right glyphicon-chevron-left');
    $('.row-offcanvas').toggleClass('active');
    $('#lg-menu').toggleClass('hidden-xs').toggleClass('visible-xs');
    $('#xs-menu').toggleClass('visible-xs').toggleClass('hidden-xs');
    $('#btnShow').toggle();
});

loadInto = function(inLink,cell)
{
	jQuery.get(inLink, {}, function(data) 
	{
		cell.html(data);
	});
}

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

	
});

doResize = function() 
{
	var fixedheight = 160;
	var sofarused = 0;
	var totalwidth = 0;
	var rownum = 0;
	var totalavailable = $(".masonry-grid ").width() - 50; 
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
		
		var h;
		if( useimage)
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
			//TODO: set the height of this row
			var overage = totalavailable / sofarused;
			var newheight = fixedheight * overage;
			var roundedheight = Math.floor(newheight);
			
			$.each( row, function()
				{
					var newcell = this;
					var newwidth = Math.floor(newheight * newcell.aspect); 
					
					jQuery("#emthumbholder img",newcell.cell).height(roundedheight); //TODO: Fix aspect
					jQuery("#emthumbholder img",newcell.cell).width(newwidth);
					//newcell.cell.width(newwidth - 30);
					jQuery(".caption a", newcell.cell).html(rownum );
				}	
			);
			row = [];
			sofarused = 0;
			rownum = rownum + 1;
		}
		
		sofarused = sofarused + neww;
		row.push( {cell:$(cell), aspect:a, width:w, height:h} );		
		
	});
	
	var overage = totalavailable / sofarused;
	var newheight = fixedheight * overage;
	var roundedheight = Math.floor(newheight);
	$.each( row, function()
		{
			var newcell = this;
			var newwidth = Math.floor(newheight * newcell.aspect); 
			if( roundedheight > fixedheight)
			{
				roundedheight = fixedheight; 
			}
			jQuery("#emthumbholder img",newcell.cell).height(roundedheight);
			//jQuery("#emthumbholder img",newcell.cell).width(newwidth);
			jQuery(".caption a", newcell.cell).html(rownum);
		}	
	);

	

}

$(window).resize(function(){
	doResize();
});
