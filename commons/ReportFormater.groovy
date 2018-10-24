package commons

import java.text.DecimalFormat

public class ReportFormater{
	
	static DecimalFormat currencyFormatter
	static {
		currencyFormatter = new DecimalFormat("###,###,###.##")
		currencyFormatter.setMinimumFractionDigits(2)
	}
	
	static DecimalFormat currencyFormatterPrice
	static {
		currencyFormatterPrice = new DecimalFormat("###,###,###.##")
		currencyFormatterPrice.setMinimumFractionDigits(4)
	}

	public static String getDashedLine() {
		  return String.format("%132s"," ").replace(' ','-')
	}
}