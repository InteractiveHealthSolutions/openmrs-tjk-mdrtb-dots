

function _isInteger(val) {
	var digits="1234567890";
	for (var i=0; i < val.length; i++) {
		if (digits.indexOf(val.charAt(i))==-1) { return false; }
		}
	return true;
	}
function _getInt(str,i,minlength,maxlength) {
	for (var x=maxlength; x>=minlength; x--) {
		var token=str.substring(i,i+x);
		if (token.length < minlength) { return null; }
		if (_isInteger(token)) { return token; }
		}
	return null;
	}
function LZ(x) {return(x<0||x>9?"":"0")+x};
	
	
var openTest;
DateInput = (function($) { // Localise the $ function
	
function DateInput(el, opts) {	
  if (typeof(opts) != "object") opts = {};
  $.extend(this, DateInput.DEFAULT_OPTS, opts);
  this.input = $(el);
  this.bindMethodsToObj("show", "hide", "hideIfClickOutside", "selectDate", "prevMonth", "nextMonth", "chooseMonth", "chooseYear");
  
  this.build();
  this.selectDate();
  this.hide();
};
DateInput.DEFAULT_OPTS = {
  month_names: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
  short_month_names: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
  short_day_names: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
  start_of_week: 0
};
DateInput.prototype = {
  build: function() {
    this.monthNameSpan = $('<span class="month_name"></span>');
    var monthNav = $('<p class="month_nav"></p>').append(
      $('<a href="#" class="prev">&laquo;</a>').click(this.prevMonth),
      " ", this.monthNameSpan, " ",
      $('<a href="#" class="next">&raquo;</a>').click(this.nextMonth)
    );
    
    var tableShell = "<table><thead><tr>";
    //build localized day_names array;
    var shortDayNames = new Array();
    try{
    for (var m = 0; m<7; m++)
       shortDayNames[m] = DAY_NAMES[m+7];  
    }catch(e){
       shortDayNames = short_day_names;
    }   
    $(this.adjustDays(shortDayNames)).each(function() {
      tableShell += "<th>" + this + "</th>";
    });
    tableShell += "</tr></thead><tbody></tbody></table>";
    
    this.dateSelector = this.rootLayers = $('<div class="date_selector"></div>')
      .css({ display: "none", position: "absolute", zIndex: 100 })
      .append(monthNav, tableShell)
      .appendTo(document.body);
    
    if ($.browser.msie && $.browser.version < 7) {
      this.ieframe = $('<iframe class="date_selector_ieframe" frameborder="0" src="#"></iframe>')
        .css({ position: "absolute", display: "none", zIndex: 99 })
        .insertBefore(this.dateSelector);
      this.rootLayers = this.rootLayers.add(this.ieframe);
    };
    
    this.tbody = $("tbody", this.dateSelector);
    
    // The anon function ensures the event is discarded
    this.input.change(this.bindToObj(function() { this.selectDate(); }));
  },
  
  selectMonth: function(date, setDropdowns) {
  	var date = new Date(date);
    this.currentMonth = date;
    
    var rangeStart = this.rangeStart(date), rangeEnd = this.rangeEnd(date);
    var numDays = this.daysBetween(rangeStart, rangeEnd);
    var dayCells = "";
    
    for (var i = 0; i <= numDays; i++) {
      var currentDay = new Date(rangeStart.getFullYear(), rangeStart.getMonth(), rangeStart.getDate() + i);
      
      if (this.isFirstDayOfWeek(currentDay)) dayCells += "<tr>";
      
      if (currentDay.getMonth() == date.getMonth()) {
        dayCells += '<td date="' + this.dateToString(currentDay) + '"><a href="#">' + currentDay.getDate() + '</a></td>';
      } else {
        dayCells += '<td class="unselected_month" date="' + this.dateToString(currentDay) + '">' + currentDay.getDate() + '</td>';
      };
      
      if (this.isLastDayOfWeek(currentDay)) dayCells += "</tr>";
    };
    
    //build the dropdowns
    if (setDropdowns){
    
	    //month select
	    var monthStr = "<select class='monthdropdown' id='monthdropdown'>";
	    for (var i = 0; i < 12;i++){
	    	monthStr += "<option value='" + i + "' style='background-color:white;padding: 1px 5px;' class='monthdropdownselect'>"+MONTH_NAMES[i]+"</option>";
	    }
	    monthStr += "</select>"; 
	    //year select
	    var yearStr = "<select class='yeardropdown' id='yeardropdown'>";
	    for (var i = date.getFullYear() - 120; i < date.getFullYear()+20;i++){
	    	yearStr += "<option value='" + i + "' style='background-color:white;padding: 1px 5px;' class='yeardropdownselect'>"+i+"</option>";
	    }
	    yearStr += "</select>";
	    this.monthNameSpan.empty().append(monthStr + " " + yearStr);
	    $('.monthdropdown').val(MONTH_NAMES[date.getMonth()]);
	    $('.yeardropdown').val(date.getFullYear());	
	    
		$('.monthdropdown').bind("change",this.bindToObj(function(event) {
		      this.chooseMonth($(event.target).val());
		      return false; 
		    }));
		 $('.yeardropdown').bind("change",this.bindToObj(function(event) {
		      this.chooseYear($(event.target).val());
		      return false;
		    }));
   	    
   	}
    this.tbody.empty().append(dayCells);
    
    $("a", this.tbody).click(this.bindToObj(function(event) {
      this.selectDate(this.stringToDate($(event.target).parent().attr("date")));
      this.hide();
      //HERE
      var tmp = $(document.body).find(".date_selector");
   		$(tmp).replaceWith("");
      return false;
    }));
    
    $("td[date=" + this.dateToString(new Date()) + "]", this.tbody).addClass("today");
    
  },
  
  selectDate: function(date) {
  	
    if (typeof(date) == "undefined") {
          this.hide();
      date = this.stringToDate(this.input.val());
    };
    
    if (date) {
     this.hide();
      this.selectedDate = date;
      this.selectMonth(date, openTest);
      var stringDate = this.dateToString(date);
      $('td[date=' + stringDate + ']', this.tbody).addClass("selected");
      this.hide();
      if (this.input.val() != stringDate) {
        this.input.val(stringDate).change();
        openTest = true;
      }
    } else {
      this.selectMonth(new Date(), openTest);
    }
     openTest = false;
  },
  
  show: function() {
    this.rootLayers.css("display", "block");
    this.setPosition();
    this.input.unbind("focus", this.show);
    $([window, document.body]).click(this.hideIfClickOutside);
  },
  
  hide: function() {
    
    this.input.focus(this.show);
    $([window, document.body]).unbind("click", this.hideIfClickOutside);
    this.rootLayers.css("display", "none");
  },
  
  hideIfClickOutside: function(event) {
    if (event.target != this.input[0] && (!this.insideSelector(event) && (event.target != "[object HTMLOptionElement]") && event.target.type != "select-one")) {
      
      this.hide();
       var tmp = $(document.body).find(".date_selector");
   		$(tmp).replaceWith("");
    };
  },
  
  stringToDate: function(string) {
  	format=dateFormat;
   	val=string+"";
	format=format+"";
	var i_val=0;
	var i_format=0;
	var c="";
	var token="";
	var token2="";
	var x,y;
	var now=new Date();
	var year=now.getYear();
	var month=now.getMonth()+1;
	var date=1;
	var hh=now.getHours();
	var mm=now.getMinutes();
	var ss=now.getSeconds();
	var ampm="";
	
	while (i_format < format.length) {
		// Get next token from format string
		c=format.charAt(i_format);
		token="";
		while ((format.charAt(i_format)==c) && (i_format < format.length)) {
			token += format.charAt(i_format++);
			}
		// Extract contents of value based on format token
		if (token=="yyyy" || token=="yy" || token=="y") {
			if (token=="yyyy") { x=4;y=4; }
			if (token=="yy")   { x=2;y=2; }
			if (token=="y")    { x=2;y=4; }
			year=_getInt(val,i_val,x,y);
			if (year==null) { return 0; }
			i_val += year.length;
			if (year.length==2) {
				if (year > 70) { year=1900+(year-0); }
				else { year=2000+(year-0); }
				}
			}
		else if (token=="MMM"||token=="NNN"){
			month=0;
			for (var i=0; i<MONTH_NAMES.length; i++) {
				var month_name=MONTH_NAMES[i];
				if (val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase()) {
					if (token=="MMM"||(token=="NNN"&&i>11)) {
						month=i+1;
						if (month>12) { month -= 12; }
						i_val += month_name.length;
						break;
						}
					}
				}
			if ((month < 1)||(month>12)){return 0;}
			}
		else if (token=="EE"||token=="E"){
			for (var i=0; i<DAY_NAMES.length; i++) {
				var day_name=DAY_NAMES[i];
				if (val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase()) {
					i_val += day_name.length;
					break;
					}
				}
			}
		else if (token=="MM"||token=="M") {
			month=_getInt(val,i_val,token.length,2);
			if(month==null||(month<1)||(month>12)){return 0;}
			i_val+=month.length;}
		else if (token=="dd"||token=="d") {
			date=_getInt(val,i_val,token.length,2);
			if(date==null||(date<1)||(date>31)){return 0;}
			i_val+=date.length;}
		else if (token=="hh"||token=="h") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<1)||(hh>12)){return 0;}
			i_val+=hh.length;}
		else if (token=="HH"||token=="H") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<0)||(hh>23)){return 0;}
			i_val+=hh.length;}
		else if (token=="KK"||token=="K") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<0)||(hh>11)){return 0;}
			i_val+=hh.length;}
		else if (token=="kk"||token=="k") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<1)||(hh>24)){return 0;}
			i_val+=hh.length;hh--;}
		else if (token=="mm"||token=="m") {
			mm=_getInt(val,i_val,token.length,2);
			if(mm==null||(mm<0)||(mm>59)){return 0;}
			i_val+=mm.length;}
		else if (token=="ss"||token=="s") {
			ss=_getInt(val,i_val,token.length,2);
			if(ss==null||(ss<0)||(ss>59)){return 0;}
			i_val+=ss.length;}
		else if (token=="a") {
			if (val.substring(i_val,i_val+2).toLowerCase()=="am") {ampm="AM";}
			else if (val.substring(i_val,i_val+2).toLowerCase()=="pm") {ampm="PM";}
			else {return 0;}
			i_val+=2;}
		else {
			if (val.substring(i_val,i_val+token.length)!=token) {return 0;}
			else {i_val+=token.length;}
			}
		}
	// If there are any trailing characters left in the value, it doesn't match
	if (i_val != val.length) { return 0; }
	// Is date valid for month?
	if (month==2) {
		// Check for leap year
		if ( ( (year%4==0)&&(year%100 != 0) ) || (year%400==0) ) { // leap year
			if (date > 29){ return 0; }
			}
		else { if (date > 28) { return 0; } }
		}
	if ((month==4)||(month==6)||(month==9)||(month==11)) {
		if (date > 30) { return 0; }
		}
	// Correct hours value
	if (hh<12 && ampm=="PM") { hh=hh-0+12; }
	else if (hh>11 && ampm=="AM") { hh-=12; }
	var newdate=new Date(year,month-1,date,hh,mm,ss);
	return newdate.getTime();
  },
  
  dateToString: function(date) {
  	var date = new Date(date);
  	var format=dateFormat;
    format=format+"";
	var result="";
	var i_format=0;
	var c="";
	var token="";
	var y=date.getYear()+"";
	var M=date.getMonth()+1;
	var d=date.getDate();
	var E=date.getDay();
	var H=date.getHours();
	var m=date.getMinutes();
	var s=date.getSeconds();
	var yyyy,yy,MMM,MM,dd,hh,h,mm,ss,ampm,HH,H,KK,K,kk,k;
	// Convert real date parts into formatted versions
	var value=new Object();
	if (y.length < 4) {y=""+(y-0+1900);}
	value["y"]=""+y;
	value["yyyy"]=y;
	value["yy"]=y.substring(2,4);
	value["M"]=M;
	value["MM"]=LZ(M);
	value["MMM"]=MONTH_NAMES[M-1];
	value["NNN"]=MONTH_NAMES[M+11];
	value["d"]=d;
	value["dd"]=LZ(d);
	value["E"]=DAY_NAMES[E+7];
	value["EE"]=DAY_NAMES[E];
	value["H"]=H;
	value["HH"]=LZ(H);
	if (H==0){value["h"]=12;}
	else if (H>12){value["h"]=H-12;}
	else {value["h"]=H;}
	value["hh"]=LZ(value["h"]);
	if (H>11){value["K"]=H-12;} else {value["K"]=H;}
	value["k"]=H+1;
	value["KK"]=LZ(value["K"]);
	value["kk"]=LZ(value["k"]);
	if (H > 11) { value["a"]="PM"; }
	else { value["a"]="AM"; }
	value["m"]=m;
	value["mm"]=LZ(m);
	value["s"]=s;
	value["ss"]=LZ(s);
	while (i_format < format.length) {
		c=format.charAt(i_format);
		token="";
		while ((format.charAt(i_format)==c) && (i_format < format.length)) {
			token += format.charAt(i_format++);
			}
		if (value[token] != null) { result=result + value[token]; }
		else { result=result + token; }
		}
	return result;
  },
  
  setPosition: function() {
    var offset = this.input.offset();
    this.rootLayers.css({
      top: offset.top + this.input.outerHeight(),
      left: offset.left
    });
    
    if (this.ieframe) {
      this.ieframe.css({
        width: this.dateSelector.outerWidth(),
        height: this.dateSelector.outerHeight()
      });
    };
  },
  
  moveMonthBy: function(amount) {
    var date = new Date(this.currentMonth.setMonth(this.currentMonth.getMonth() + amount));
    this.selectMonth(date, true);
  },
  
  prevMonth: function() {
    this.moveMonthBy(-1);
    return false;
  },
  
  chooseMonth: function(month) {
  var date = new Date(this.currentMonth.setMonth(month));
   this.selectMonth(date, true);
    return false;
  },
  
  chooseYear: function(year) {
   var date = new Date(this.currentMonth.setYear(year));
   this.selectMonth(date, true);
   return false;
  },
  
  nextMonth: function() {
    this.moveMonthBy(1);
    return false;
  },
  
  monthName: function(date) {
    return this.month_names[date.getMonth()];
  },
  
  insideSelector: function(event) {
    var offset = this.dateSelector.offset();
    offset.right = offset.left + this.dateSelector.outerWidth();
    offset.bottom = offset.top + this.dateSelector.outerHeight();
    
    return event.pageY < offset.bottom &&
           event.pageY > offset.top &&
           event.pageX < offset.right &&
           event.pageX > offset.left;
  },
  
  bindToObj: function(fn) {
    var self = this;
    return function() { return fn.apply(self, arguments) };
  },
  
  bindMethodsToObj: function() {
    for (var i = 0; i < arguments.length; i++) {
      this[arguments[i]] = this.bindToObj(this[arguments[i]]);
    };
  },
  
  indexFor: function(array, value) {
    for (var i = 0; i < array.length; i++) {
      if (value == array[i]) return i;
    };
  },
  
  monthNum: function(month_name) {
    return this.indexFor(this.month_names, month_name);
  },
  
  shortMonthNum: function(month_name) {
    return this.indexFor(this.short_month_names, month_name);
  },
  
  shortDayNum: function(day_name) {
    return this.indexFor(this.short_day_names, day_name);
  },
  
  daysBetween: function(start, end) {
    start = Date.UTC(start.getFullYear(), start.getMonth(), start.getDate());
    end = Date.UTC(end.getFullYear(), end.getMonth(), end.getDate());
    return (end - start) / 86400000;
  },
  
  changeDayTo: function(to, date, direction) {
    var difference = direction * (Math.abs(date.getDay() - to - (direction * 7)) % 7);
    return new Date(date.getFullYear(), date.getMonth(), date.getDate() + difference);
  },
  
  rangeStart: function(date) {
    var dateTmp = new Date(date);
    return this.changeDayTo(this.start_of_week, new Date(dateTmp.getFullYear(), dateTmp.getMonth()), -1);
  },
  
  rangeEnd: function(date) {
  	var dateTmp = new Date(date);
    return this.changeDayTo((this.start_of_week - 1) % 7, new Date(dateTmp.getFullYear(), dateTmp.getMonth() + 1, 0), 1);
  },
  
  isFirstDayOfWeek: function(date) {
    return date.getDay() == this.start_of_week;
  },
  
  isLastDayOfWeek: function(date) {
    return date.getDay() == (this.start_of_week - 1) % 7;
  },
  
  adjustDays: function(days) {
    var newDays = [];
    for (var i = 0; i < days.length; i++) {
      newDays[i] = days[(i + this.start_of_week) % 7];
    };
    return newDays;
  }
};

$.fn.date_input = function(opts) {
  openTest = true;
  return this.each(function() { new DateInput(this, opts); });
};
$.date_input = { initialize: function(opts) {
  $("input.date_input").date_input(opts);
} };

return DateInput;
})(jQuery); // End localisation of the $ function
