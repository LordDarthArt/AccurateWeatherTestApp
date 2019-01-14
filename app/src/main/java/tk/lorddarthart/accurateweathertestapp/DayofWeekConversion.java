package tk.lorddarthart.accurateweathertestapp;

public class DayofWeekConversion {
    public String convert(String code){
        switch (code){
            case  "Mon":{
                return "Monday";
            }
            case  "Tue":{
                return "Tuesday";
            }
            case  "Wed":{
                return "Wednesday";
            }
            case  "Thu":{
                return "Thusday";
            }
            case  "Fri":{
                return "Friday";
            }
            case  "Sat":{
                return "Saturday";
            }
            case  "Sun":{
                return "Sunday";
            }
        }
        return "";
    }
}
