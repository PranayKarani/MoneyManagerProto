package com.moneymanager.activities.stats;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.moneymanager.Common;
import com.moneymanager.R;
import com.moneymanager.activities.transaction.AEditTransaction;
import com.moneymanager.entities.Account;
import com.moneymanager.entities.Transaction;
import com.moneymanager.exceptions.NoAccountsException;
import com.moneymanager.repo.TAccounts;
import com.moneymanager.repo.TTransactions;
import com.moneymanager.utilities.MyCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.moneymanager.Common.*;

public class AStats extends AppCompatActivity {

	private static boolean startDateSelected;
	private static Date customStartDate;
	private static Date customEndDate;
	private static AlertDialog customDatePickerDialog;
	private final int DAY = 324;
	private final int WEEK = 244;
	private final int MONTH = 824;
	private final int YEAR = 944;
	private final int CUSTOM = 31;
	private Date myDate;
	private SimpleDateFormat sdf;
	private int selectedAccountID = CURRENT_ACCOUNT_ID;
	private int selectedPeriod = DAY;
	private Account[] accounts;
	private String[] acc_names;
	private int[] acc_ids;
	// Views
	private MenuItem period_text;
	private TextView cardIncomeTextView, cardExpenseTextView, cardTotalTextView;

	private LinearLayout bargraphCard;
	private CardView income_trans_container_card, expense_trans_container_card;
	private LinearLayout income_trans_container, expense_trans_container;


	// Colors
	private int colorGreen;
	private int colorFaintGreen;
	private int colorRed;
	private int colorFaintRed;
	private int colorTransparent;
	private int colorWhite;
	private int colorPrimaryDark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_stats);

		init();

		setupToolbar(this, R.id.a_stats_toolbar, "");

		refreshToolbar();

		bargraphCard = (LinearLayout) findViewById(R.id.a_stats_overview_bargraph);

		refreshDataLayouts();

		income_trans_container_card = (CardView) findViewById(R.id.a_stats_income_trans_list_container_card);
		income_trans_container = (LinearLayout) findViewById(R.id.a_stats_income_trans_list_container);
		expense_trans_container_card = (CardView) findViewById(R.id.a_stats_expense_trans_list_container_card);
		expense_trans_container = (LinearLayout) findViewById(R.id.a_stats_expense_trans_list_container);


		cardIncomeTextView = (TextView) findViewById(R.id.a_stats_overview_card_income_amt);
		cardExpenseTextView = (TextView) findViewById(R.id.a_stats_overview_card_expense_amt);
		cardTotalTextView = (TextView) findViewById(R.id.a_stats_overview_card_total_amt);

		Calendar c = Calendar.getInstance();

	}

	@Override
	protected void onResume() {
		super.onResume();
		Bundle b = new Bundle();
		b.putString("date", sdf.format(myDate));
		b.putString("cus_end_date", sdf.format(customEndDate));
		b.putString("cus_start_date", sdf.format(customStartDate));
		new TransListLoader().execute(b);
	}

	private void init() {

		sdf = MyCalendar.getSimpleDateFormat();
		final Intent intent = getIntent();
		try {
			myDate = sdf.parse(intent.getStringExtra("date"));
			customStartDate = myDate;
			customEndDate = myDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}

//		Toast.makeText(this, "stats for " + MyCalendar.dateToString(myDate), Toast.LENGTH_SHORT).show();

		// setup Account related Arrays
		TAccounts accTable = new TAccounts(this);
		try {
			accounts = accTable.getAllAccounts(TAccounts.NAME, null);
		} catch (NoAccountsException e) {
			e.printStackTrace();
		}
		acc_names = new String[accounts.length + 1];
		acc_names[0] = "All Accounts";
		acc_ids = new int[accounts.length + 1];
		acc_ids[0] = ALL_ACCOUNT_ID;
		for (int i = 1; i < acc_names.length; i++) {
			acc_names[i] = accounts[i - 1].getName();
			acc_ids[i] = accounts[i - 1].getId();
			if (acc_ids[i] == CURRENT_ACCOUNT_ID) {
				CURRENT_ACCOUNT_NAME = acc_names[i];
			}
		}

		// setup COlotrs
		colorGreen = getMyColor(this, R.color.colorGreen);
		colorFaintGreen = getMyColor(this, R.color.colorFaintGreen);
		colorRed = getMyColor(this, R.color.colorRed);
		colorFaintRed = getMyColor(this, R.color.colorFaintRed);
		colorWhite = getMyColor(this, R.color.colorWhite);
		colorTransparent = getMyColor(this, R.color.transparent);
		colorPrimaryDark = getMyColor(this, R.color.colorPrimaryDark);

	}

	public void onStartDatePick(View view) {
		startDateSelected = true;
		final DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.show(getSupportFragmentManager(), "Pick Starting Date");
	}

	public void onEndDatePick(View view) {
		startDateSelected = false;
		final DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.show(getSupportFragmentManager(), "Pick Ending Date");
	}

	private void refreshDataLayouts() {

		switch (selectedPeriod) {

			case DAY:
				bargraphCard.setVisibility(View.GONE);
				break;
			case WEEK:
				bargraphCard.setVisibility(View.VISIBLE);
				break;
			case MONTH:
				bargraphCard.setVisibility(View.VISIBLE);
//				calenderCard.setVisibility(View.VISIBLE);
				break;
			case YEAR:
				bargraphCard.setVisibility(View.VISIBLE);
				break;
			default:
				bargraphCard.setVisibility(View.GONE);
				break;

		}

	}

	/**
	 * Menu stuff
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.stats_period_menu, menu);
		period_text = menu.getItem(0);
		period_text.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Toast.makeText(AStats.this, "show particular " + period_text.getTitle() + " picker", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		final Bundle b = new Bundle();
		b.putString("date", sdf.format(myDate));
		b.putString("cus_end_date", sdf.format(customEndDate));
		b.putString("cus_start_date", sdf.format(customStartDate));

		switch (item.getItemId()) {
			case R.id.stats_menu_period_text:
				// show selector for selected period
				// e.g. if month is selected and this is tapped, show dialog for selecting other month
				break;
			case R.id.stats_menu_period_day:
				period_text.setTitle("Day");

				selectedPeriod = DAY;
				refreshDataLayouts();
				new TransListLoader().execute(b);

				break;
			case R.id.stats_menu_period_week:
				period_text.setTitle("Week");

				selectedPeriod = WEEK;
				refreshDataLayouts();

				new TransListLoader().execute(b);

				break;
			case R.id.stats_menu_period_month:
				period_text.setTitle("Month");

				selectedPeriod = MONTH;
				refreshDataLayouts();

				new TransListLoader().execute(b);
				break;
			case R.id.stats_menu_period_year:
				period_text.setTitle("Year");

				selectedPeriod = YEAR;

				break;
			default: // custom

				selectedPeriod = CUSTOM;
				customDatePickerDialog = new AlertDialog.Builder(this)
						.setView(R.layout.d_custom_period_picker)
						.setPositiveButton("Go", null)
						.setCancelable(true)
						.create();
				customDatePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
					@Override
					public void onShow(final DialogInterface dialogX) {
						final Button button = customDatePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE);

						final TextView startDateText = (TextView) customDatePickerDialog.findViewById(R.id.d_custom_period_starting_date);
						startDateText.setText(MyCalendar.getNiceFormatedCompleteDateString(customStartDate));
						final TextView endDateText = (TextView) customDatePickerDialog.findViewById(R.id.d_custom_period_ending_date);
						endDateText.setText(MyCalendar.getNiceFormatedCompleteDateString(customStartDate));

						button.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Log.i(mylog, "start date pick: " + customStartDate.getTime());
								Log.i(mylog, "end date pick: " + customEndDate.getTime());
								if (customStartDate.getTime() > customEndDate.getTime()) {
									// start date cannot be greater than end date
									Toast.makeText(AStats.this, "start date cannot be greater than end date", Toast.LENGTH_SHORT).show();
								} else {
									b.putString("cus_end_date", sdf.format(customEndDate));
									b.putString("cus_start_date", sdf.format(customStartDate));
									new TransListLoader().execute(b);
									refreshDataLayouts();
									customDatePickerDialog.dismiss();
									period_text.setTitle("Custom");
								}
							}

						});

					}
				});
				customDatePickerDialog.show();
				break;
		}


		return true;
	}

	private void refreshToolbar() {
		final LinearLayout layout = (LinearLayout) findViewById(R.id.a_stats_toolbar_layout);

		final TextView toolbar_text = (TextView) layout.findViewById(R.id.a_stats_toolbar_account_text);
		toolbar_text.setText(CURRENT_ACCOUNT_NAME);

		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(AStats.this);
				builder.setCancelable(true);
				builder.setTitle("Select an Account");
				builder.setItems(acc_names, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Log.i(mylog, "selected account: " + acc_names[i]);
						toolbar_text.setText(acc_names[i]);
						dialogInterface.dismiss();
						selectedAccountID = acc_ids[i];

						// refresh the stats according to selected account
						Bundle b = new Bundle();
						b.putString("date", sdf.format(myDate));
						b.putString("cus_start_date", sdf.format(customStartDate));
						b.putString("cus_end_date", sdf.format(customEndDate));
						new TransListLoader().execute(b);

					}
				});
				builder.create().show();
			}
		});
	}

	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar cal = Calendar.getInstance();
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, y, m, d);
		}

		public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

			year -= 1900;//
			Date newDate = new Date(year, month, dayOfMonth);

			if (startDateSelected) {
				customStartDate = newDate;
			} else {
				customEndDate = newDate;
			}

			final TextView text = (TextView) customDatePickerDialog.findViewById((startDateSelected ? R.id.d_custom_period_starting_date : R.id.d_custom_period_ending_date));
			text.setText(MyCalendar.getNiceFormatedCompleteDateString(newDate));
		}

	}

	class TransListLoader extends AsyncTask<Bundle, Void, Transaction[]> {

		final double[] weeklyIncomeSums = {0, 0, 0, 0, 0, 0, 0};// e.g. 2000, 0, 0 , 0, 10000, 0, 0
		final double[] weeklyExpenseSums = {0, 0, 0, 0, 0, 0, 0};// e.g. 500, 1000, 40 , 240, 300, 0, 240

		final double[] monthlyIncomeSums = new double[Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)];
		final double[] monthlyExpenseSums = new double[Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)];

		private final String incomeString = "income";
		private final String expenseString = "expense";
		Transaction[] transactions;
		ArrayList<Transaction> incomeTransactions = null;
		ArrayList<Transaction> expenseTransactions = null;
		double incomeSum = 0;
		double expenseSum = 0;
		int countIncomeTrans = 0;
		int countExpenseTrans = 0;
		String weekStartDateString, weekEndDateString;

		// Views
		PieChart main_piechart;
		TextView pchart_text;
		BarChart weekBarChart, monthBarChart;
		TextView bchart_text;

		AlertDialog piechart_dialog;
		AlertDialog barchart_dialog;


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i(mylog, "loader started");

			// fill up monthly sums
			for (int i = 0; i < monthlyIncomeSums.length; i++) {
				monthlyIncomeSums[i] = 0;
				monthlyExpenseSums[i] = 0;
			}

			//setting up Piechart
			main_piechart = (PieChart) findViewById(R.id.a_stats_piechart);
			pchart_text = (TextView) findViewById(R.id.a_stats_overview_piechart_text);

			// setting up bar chart
			weekBarChart = (BarChart) findViewById(R.id.a_stats_week_barchart);
			monthBarChart = (BarChart) findViewById(R.id.a_stats_month_barchart);
			bchart_text = (TextView) findViewById(R.id.a_stats_overview_barchart_text);

		}

		@Override
		protected Transaction[] doInBackground(Bundle... params) {

			Bundle bundle = params[0];

			TTransactions tTransactions = new TTransactions(AStats.this);

			Transaction[] transactions = null;

			switch (selectedPeriod) {

				case DAY:

					try {
						Date date = sdf.parse(bundle.getString("date"));

						if (selectedAccountID == ALL_ACCOUNT_ID) {
							transactions = tTransactions.getTransactionsForDay(date);
						} else {
							transactions = tTransactions.getAccountSpecificTransactionsForDay(selectedAccountID, date);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;

				case WEEK:
					try {
						Date date = sdf.parse(bundle.getString("date"));

						if (selectedAccountID == ALL_ACCOUNT_ID) {
							transactions = tTransactions.getTransactionsForWeek(date);
						} else {
							transactions = tTransactions.getAccountSpecificTransactionsForWeek(selectedAccountID, date);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;

				case MONTH:
					try {
						Date date = sdf.parse(bundle.getString("date"));

						if (selectedAccountID == ALL_ACCOUNT_ID) {
							transactions = tTransactions.getTransactionsForMonth(date);
						} else {
							transactions = tTransactions.getAccountSpecificTransactionsForMonth(selectedAccountID, date);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;
				default:
					try {
						Date startdate = sdf.parse(bundle.getString("cus_start_date"));
						Date enddate = sdf.parse(bundle.getString("cus_end_date"));

						if (selectedAccountID == ALL_ACCOUNT_ID) {
							transactions = tTransactions.getTransactionsForCustomPeriod(startdate, enddate);
						} else {
							transactions = tTransactions.getAccountSpecificTransactionsForCustomPeriod(selectedAccountID, startdate, enddate);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;

			}


			return transactions;
		}

		@Override
		protected void onPostExecute(Transaction[] transactions) {
			super.onPostExecute(transactions);

			this.transactions = transactions;
			incomeTransactions = new ArrayList<>();
			expenseTransactions = new ArrayList<>();


			// setting up transactions list
			income_trans_container.removeAllViews();
			expense_trans_container.removeAllViews();

			if (transactions.length > 0) {
				fillTransList(R.layout.x_stats_row, null, transactions, true);
			}

			income_trans_container_card.setVisibility((countIncomeTrans == 0) ? View.GONE : View.VISIBLE);
			expense_trans_container_card.setVisibility((countExpenseTrans == 0) ? View.GONE : View.VISIBLE);


			// setting up overview card
			TextView title = (TextView) findViewById(R.id.a_stats_overview_card_title);
			switch (selectedPeriod) {
				case DAY:
					title.setText(MyCalendar.dateToString(myDate) + " " + MyCalendar.monthToFullString(myDate) + "'s Overview");
					break;
				case WEEK:
					Date[] dates = MyCalendar.weekEndandStartDatesforDate(myDate);
					weekStartDateString = MyCalendar.dateToString(dates[0]) + " " + MyCalendar.monthToString(dates[0]);
					weekEndDateString = MyCalendar.dateToString(dates[1]) + " " + MyCalendar.monthToString(dates[1]);
					title.setText(weekStartDateString + " ~ " + weekEndDateString + " Overview");
					break;
				case MONTH:
					String text = MyCalendar.monthToFullString(myDate) + "'s Overview";
					title.setText(text);
					break;
				case YEAR:
					break;
				default:// Custom
					if (customStartDate.equals(customEndDate)) {
						String startDateText = MyCalendar.dateToString(customStartDate) + " " + MyCalendar.monthToFullString(customStartDate);
						title.setText(startDateText + "'s Overview");
					} else {
						String startDateText = MyCalendar.dateToString(customStartDate) + " " + MyCalendar.monthToString(customStartDate);
						String endDateText = MyCalendar.dateToString(customEndDate) + " " + MyCalendar.monthToString(customEndDate);
						title.setText(startDateText + " ~ " + endDateText + " Overview");
					}
					break;

			}
			cardExpenseTextView.setText("Rs " + expenseSum);
			cardIncomeTextView.setText("Rs " + incomeSum);
			cardTotalTextView.setText("Rs " + (incomeSum - expenseSum));


			if (transactions.length == 0) {
				pchart_text.setVisibility(View.VISIBLE);
				main_piechart.setVisibility(View.GONE);

				bchart_text.setVisibility(View.VISIBLE);
				weekBarChart.setVisibility(View.GONE);
				monthBarChart.setVisibility(View.GONE);

			} else {
				pchart_text.setVisibility(View.GONE);
				main_piechart.setVisibility(View.VISIBLE);
				setUpPieChart();

				bchart_text.setVisibility(View.GONE);

				switch (selectedPeriod) {
					case WEEK:
						setUpWeekBarGarph();
						monthBarChart.setVisibility(View.GONE);
						weekBarChart.setVisibility(View.VISIBLE);
						break;
					case MONTH:
						setUpMonthBarGraph();
						weekBarChart.setVisibility(View.GONE);
						monthBarChart.setVisibility(View.VISIBLE);
						break;
					default:
						break;
				}
			}

			Log.i(mylog, "loader done loading");
		}

		private void setUpPieChart() {

			piechart_dialog = new AlertDialog.Builder(AStats.this)
					.setView(R.layout.d_stats_piechart)
					.setCancelable(true)
					.create();

			main_piechart.setVisibility(View.VISIBLE);
			main_piechart.setHoleColor(colorTransparent);
			main_piechart.setHoleRadius(50);
			main_piechart.setDrawCenterText(false);
			main_piechart.setRotationEnabled(false);
			main_piechart.getLegend().setEnabled(false);
			main_piechart.getDescription().setEnabled(false);
			main_piechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
				@Override
				public void onValueSelected(Entry e, Highlight h) {
					final String entryText = ((PieEntry) e).getLabel();
					final int type = entryText.equals(incomeString) ? INCOME : EXPENSE;
					piechart_dialog.setOnShowListener(new DialogInterface.OnShowListener() {

						@Override
						public void onShow(DialogInterface dialog) {

							final LinearLayout container_layout = (LinearLayout) piechart_dialog.findViewById(R.id.d_stats_piechart_list_container);
							if (container_layout != null) {
								container_layout.removeAllViews();
							}

							String period;
							final int color = Common.getMyColor(AStats.this, type == INCOME ? R.color.colorGreen : R.color.colorRed);
							switch (selectedPeriod) {
								case DAY:
									period = "on " + MyCalendar.dateToString(myDate) + " " + MyCalendar.monthToString(myDate);
									break;
								case WEEK:
									Date[] dates = MyCalendar.weekEndandStartDatesforDate(myDate);
									String startDate = MyCalendar.dateToString(dates[0]) + " " + MyCalendar.monthToString(dates[0]);
									String endDate = MyCalendar.dateToString(dates[1]) + " " + MyCalendar.monthToString(dates[1]);
									period = "between " + startDate + " ~ " + endDate;
									break;
								case MONTH:
									period = "in " + MyCalendar.monthToFullString(myDate);
									break;
								case YEAR:
									period = "-";
									break;
								default:// Custom
									String startingDate = MyCalendar.dateToString(customStartDate) + " " + MyCalendar.monthToString(customStartDate);
									String endingDate = MyCalendar.dateToString(customEndDate) + " " + MyCalendar.monthToString(customEndDate);
									period = "between " + startingDate + " ~ " + endingDate;
									break;

							}
							final String centerText = (type == INCOME ? "Income" : "Expense") + " " + period;

							final PieChart dialog_piechart = (PieChart) piechart_dialog.findViewById(R.id.d_stats_piechart);
							dialog_piechart.setHoleColor(Common.getMyColor(AStats.this, R.color.transparent));
							dialog_piechart.setHoleRadius(60);
							dialog_piechart.setCenterTextSize(16);
							dialog_piechart.setCenterTextRadiusPercent(80);
							dialog_piechart.setCenterTextColor(color);
							dialog_piechart.setCenterText(centerText);
							dialog_piechart.getLegend().setEnabled(false);
							dialog_piechart.getDescription().setEnabled(false);


							ArrayList<PieEntry> ye = new ArrayList<>();

							final ArrayList<Transaction> tx = type == INCOME ? incomeTransactions : expenseTransactions;

							final HashMap<String, Float> cat_stats_map = new HashMap<>();

							for (Transaction t : tx) {

								final String cat_name = t.getCategory().getName();
								final double amt = t.getAmount();

								if (cat_stats_map.containsKey(cat_name)) {
									cat_stats_map.put(cat_name, (float) (cat_stats_map.get(cat_name) + amt));
								} else {
									cat_stats_map.put(cat_name, (float) amt);
								}

							}

							for (Map.Entry e : cat_stats_map.entrySet()) {

								ye.add(new PieEntry((Float) e.getValue(), String.valueOf(e.getKey())));

							}

							final PieDataSet pds = new PieDataSet(ye, "");
							pds.setColor(color);
							pds.setSliceSpace(2);
							pds.setValueTextSize(14);
							dialog_piechart.setData(new PieData(pds));
							dialog_piechart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

								@Override
								public void onValueSelected(Entry e, Highlight h) {

									dialog_piechart.setCenterText(((PieEntry) e).getLabel() + " Stats");

									// create a transaction list of the selected category
									ArrayList<Transaction> req_trans = new ArrayList<>();

									for (Transaction t : tx) {
										if (t.getCategory().getName().equals(((PieEntry) e).getLabel())) {
											req_trans.add(t);
										}
									}

									Transaction[] ttt = new Transaction[req_trans.size()];
									for (int i = 0; i < ttt.length; i++) {
										ttt[i] = req_trans.get(i);
									}

									fillTransList(R.layout.x_stats_row, container_layout, ttt, false);
								}

								@Override
								public void onNothingSelected() {
									dialog_piechart.setCenterText(centerText);
									container_layout.removeAllViews();
								}
							});
							dialog_piechart.invalidate();

						}
					});

					piechart_dialog.show();
				}

				@Override
				public void onNothingSelected() {

				}
			});

			ArrayList<PieEntry> yE = new ArrayList<>();
			ArrayList<Integer> colors = new ArrayList<>();

			if (expenseSum > 0) {
				yE.add(new PieEntry((float) expenseSum, expenseString));
				colors.add(getMyColor(AStats.this, R.color.colorRed));
			}
			if (incomeSum > 0) {
				yE.add(new PieEntry((float) incomeSum, incomeString));
				colors.add(getMyColor(AStats.this, R.color.colorGreen));
			}


			PieDataSet pds = new PieDataSet(yE, "this is some label");
			pds.setSliceSpace(2);
			pds.setValueTextSize(14);
			pds.setColors(colors);
			pds.setValueTextColor(colorPrimaryDark);

			main_piechart.setData(new PieData(pds));
			main_piechart.invalidate();

		}

		private void setUpWeekBarGarph() {

			weekBarChart.fitScreen();

			final List<BarEntry> incomeGroup = new ArrayList<>();
			final List<BarEntry> expenseGroup = new ArrayList<>();

			for (int i = 6; i >= 0; i--) {
				incomeGroup.add(new BarEntry(i, (float) weeklyIncomeSums[i], incomeString));
				expenseGroup.add(new BarEntry(i, (float) weeklyExpenseSums[i], expenseString));
			}

			final BarDataSet inSet = new BarDataSet(incomeGroup, "income");
			inSet.setColor(colorGreen);
			inSet.setValueTextColor(colorGreen);
			inSet.setValueTextSize(8);
			final BarDataSet exSet = new BarDataSet(expenseGroup, "expense");
			exSet.setColor(colorRed);
			exSet.setValueTextColor(colorRed);
			exSet.setValueTextSize(8);

			final float barSpace = 0.0f;
			final float barWidth = 0.30f;
			final float groupSpace = 1 - ((barSpace + barWidth) * 2);

			final BarData bd = new BarData(inSet, exSet);
			bd.setBarWidth(barWidth);

			final String[] weekDays = {"mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

			// Calculating weekDates
			final String[] weekDates = new String[7];

			final Date weekEnddate = MyCalendar.weekEndandStartDatesforDate(transactions[0].getDateTime())[1];
			for (int i = 0; i < 7; i++) {

				Date newDate = MyCalendar.dateBeforeDays(weekEnddate, 6 - i);
				weekDates[i] = MyCalendar.dateToString(newDate);

			}

			weekBarChart.setData(bd);
			weekBarChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
			weekBarChart.getAxisRight().setEnabled(false);
			weekBarChart.getAxisLeft().setTextColor(colorWhite);
			weekBarChart.getAxisLeft().setDrawGridLines(false);
			weekBarChart.getXAxis().setDrawGridLines(false);
			weekBarChart.setDrawBarShadow(false);
			weekBarChart.groupBars(-0.5f, groupSpace, barSpace);
			weekBarChart.getDescription().setText(weekStartDateString + " ~ " + weekEndDateString);
			weekBarChart.getDescription().setTextColor(colorWhite);
			weekBarChart.getLegend().setEnabled(false);
			final XAxis xAxis = weekBarChart.getXAxis();
			xAxis.setGranularity(1f);
			xAxis.setTextColor(colorWhite);
			xAxis.setLabelRotationAngle(315);
			xAxis.setValueFormatter(new IAxisValueFormatter() {
				@Override
				public String getFormattedValue(float value, AxisBase axis) {
					return weekDays[(int) value] + ", " + weekDates[(int) value];
				}
			});
			weekBarChart.invalidate();

		}

		private void setUpMonthBarGraph() {

			barchart_dialog = new AlertDialog.Builder(AStats.this)
					.setView(R.layout.d_stats_barchart)
					.create();

			monthBarChart.fitScreen();

			final List<BarEntry> incomeGroup = new ArrayList<>();
			final List<BarEntry> expenseGroup = new ArrayList<>();

			for (int i = monthlyIncomeSums.length - 1; i >= 0; i--) {
				incomeGroup.add(new BarEntry(i, (float) monthlyIncomeSums[i], incomeString));
				expenseGroup.add(new BarEntry(i, (float) monthlyExpenseSums[i], expenseString));
			}

			final BarDataSet inSet = new BarDataSet(incomeGroup, "income");
			inSet.setColor(colorGreen);
			inSet.setValueTextColor(colorGreen);
			inSet.setValueTextSize(8);
			final BarDataSet exSet = new BarDataSet(expenseGroup, "expense");
			exSet.setColor(colorRed);
			exSet.setValueTextColor(colorRed);
			exSet.setValueTextSize(8);

			final float barSpace = 0.0f;
			final float barWidth = 0.30f;
			final float groupSpace = 1 - ((barSpace + barWidth) * 2);

			final BarData bd = new BarData(inSet, exSet);
			bd.setDrawValues(false);
			bd.setBarWidth(barWidth);

			monthBarChart.setData(bd);
			monthBarChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
			monthBarChart.getAxisRight().setEnabled(false);
			monthBarChart.getAxisLeft().setTextColor(colorWhite);
//			monthBarChart.getAxisLeft().setDrawGridLines(false);
//			monthBarChart.getXAxis().setDrawGridLines(false);
			monthBarChart.setDrawBarShadow(false);
			monthBarChart.groupBars(-0.5f, groupSpace, barSpace);
			monthBarChart.getDescription().setText(MyCalendar.monthToFullString(myDate) + "'s Stats \n(pinch out horizontally for more precision)");
			monthBarChart.getDescription().setTextColor(colorWhite);
			monthBarChart.setDrawGridBackground(false);
			monthBarChart.getLegend().setEnabled(false);
			final XAxis xAxis = monthBarChart.getXAxis();
			xAxis.setGranularity(1f);
			xAxis.setTextColor(colorWhite);
			xAxis.setValueFormatter(new IAxisValueFormatter() {
				@Override
				public String getFormattedValue(float value, AxisBase axis) {
					return String.valueOf((int) value + 1);
				}
			});
			monthBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

				@Override
				public void onValueSelected(Entry e, Highlight h) {

					final int type = String.valueOf(e.getData()).equals(incomeString) ? INCOME : EXPENSE;
					final int selectDate = (int) e.getX() + (type == INCOME ? 2 : 1);

					barchart_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
						@Override
						public void onShow(DialogInterface dialog) {

							final LinearLayout container_layout = (LinearLayout) barchart_dialog.findViewById(R.id.d_stats_barchart_list_container);
							container_layout.removeAllViews();
							container_layout.setBackgroundColor(type == INCOME ? colorFaintGreen : colorFaintRed);

							// create a transaction list of the selected category
							ArrayList<Transaction> req_trans = new ArrayList<>();

							final ArrayList<Transaction> tx = type == INCOME ? incomeTransactions : expenseTransactions;

							for (Transaction t : tx) {
								if (t.getDateTime().getDate() == selectDate) {
									req_trans.add(t);
								}
							}

							Transaction[] ttt = new Transaction[req_trans.size()];
							for (int i = 0; i < ttt.length; i++) {
								ttt[i] = req_trans.get(i);
							}

							fillTransList(R.layout.x_stats_row, container_layout, ttt, false);

						}
					});
//					Log.i(mylog, type + "  " + selectDate + "  " + e.getY());
					if (e.getY() > 0) {
						barchart_dialog.show();
					}
				}

				@Override
				public void onNothingSelected() {

				}
			});
			monthBarChart.invalidate();

		}

		private void fillTransList(int row_layout, LinearLayout container_layout, Transaction[] trans, final boolean loadForMainScreen) {

			if (container_layout != null) {
				container_layout.removeAllViews();
			}

			Date previousDate = trans.length > 0 ? MyCalendar.dateBeforeDays(trans[0].getDateTime(), 1) : null; // used in other lists like in piechart dialog
			Date previousExpenseDate = null;

			switch (selectedPeriod) {
				case WEEK:
					if (loadForMainScreen) {
						previousExpenseDate = MyCalendar.weekEndandStartDatesforDate(trans[0].getDateTime())[1];
					}
					break;
				case MONTH:
					if (loadForMainScreen) {
						previousExpenseDate = MyCalendar.lastDateOfMonth(trans[0].getDateTime());
					}
					break;
				default:
					previousExpenseDate = MyCalendar.dateToday();// waste and unnecessary
					break;
			}
			Date previousIncomeDate = previousExpenseDate;

			Date firstIncomeDateOfWeek = null, firstExpenseDayOfWeek = null;
			for (Transaction t : trans) {

				if (firstExpenseDayOfWeek == null) {
					if (t.getCategory().getType() == EXPENSE) {
						firstExpenseDayOfWeek = t.getDateTime();
						continue;
					}
				}

				if (firstIncomeDateOfWeek == null) {
					if (t.getCategory().getType() == INCOME) {
						firstIncomeDateOfWeek = t.getDateTime();
					}
				}

			}

			int currentWeekDayIndexForIncome = 0;
			int currentWeekDayIndexForExpense = 0;
			for (int i = 0; i < trans.length; i++) {

				final Transaction t = trans[i];
				View rowView = getLayoutInflater().inflate(row_layout, null);

				rowView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						Intent intent = new Intent(AStats.this, AEditTransaction.class);
						intent.putExtra("trans_id", t.getId());
						startActivity(intent);

						if (piechart_dialog != null) {
							if (piechart_dialog.isShowing()) {
								piechart_dialog.dismiss();
							}
						}

						if (barchart_dialog != null) {
							if (barchart_dialog.isShowing()) {
								barchart_dialog.dismiss();
							}
						}

					}
				});

				final TextView tCat = (TextView) rowView.findViewById(R.id.x_stats_row_cat);
				final TextView tAmt = (TextView) rowView.findViewById(R.id.x_stats_row_amt);
				final TextView tAcc = (TextView) rowView.findViewById(R.id.x_stats_row_acc);
				final TextView tDate = (TextView) rowView.findViewById(R.id.x_stats_row_date);
				final TextView tInfo = (TextView) rowView.findViewById(R.id.x_stats_row_info);
				tInfo.setText(t.getShortInfo());
				tDate.setVisibility(View.INVISIBLE);
				final String dateText = MyCalendar.dateToString(t.getDateTime()) + "\n" + MyCalendar.monthToString(t.getDateTime());

				if (selectedAccountID == ALL_ACCOUNT_ID) {
					tAcc.setVisibility(View.VISIBLE);
					tAcc.setText(t.getAccount().getName());
				} else {
					tAcc.setVisibility(View.GONE);
				}

				tCat.setText(t.getCategory().getName());
				tAmt.setText(t.getAmountString());

				if (loadForMainScreen) {

					LinearLayout container;

					if (t.getCategory().getType() == INCOME) {

						incomeSum += t.getAmount();
						countIncomeTrans++;
						incomeTransactions.add(t);

						container = income_trans_container;
						if (!t.getDateTime().equals(previousIncomeDate)) {
							// new day
							if (!firstIncomeDateOfWeek.equals(t.getDateTime())) {
								container.addView(getLayoutInflater().inflate(R.layout.x_line, null));
							}
							tDate.setVisibility(View.VISIBLE);
							tDate.setText(dateText);
							currentWeekDayIndexForIncome += MyCalendar.daysBetween(previousIncomeDate, t.getDateTime());
						}

						if (selectedPeriod == WEEK) {
							weeklyIncomeSums[currentWeekDayIndexForIncome] += t.getAmount();
						}

						if (selectedPeriod == MONTH) {
							monthlyIncomeSums[currentWeekDayIndexForIncome] += t.getAmount();
						}

						previousIncomeDate = t.getDateTime();

					} else {

						expenseSum += t.getAmount();
						countExpenseTrans++;
						expenseTransactions.add(t);

						container = expense_trans_container;

						// is current transaction date does not match with previous expense date, it's a new date
						if (!t.getDateTime().equals(previousExpenseDate)) {

							// if current transaction date does not matches with first expense date of week
							// don't add new row layout
							// i.e. if the current transaction date is not the very first transaction date of week,
							// add a blank row
							if (!firstExpenseDayOfWeek.equals(t.getDateTime())) {
								container.addView(getLayoutInflater().inflate(R.layout.x_line, null));
							}
							tDate.setVisibility(View.VISIBLE);
							tDate.setText(dateText);
							currentWeekDayIndexForExpense += MyCalendar.daysBetween(previousExpenseDate, t.getDateTime());
						}

						if (selectedPeriod == WEEK) {
							weeklyExpenseSums[currentWeekDayIndexForExpense] += t.getAmount();
						}

						if (selectedPeriod == MONTH) {
							monthlyExpenseSums[currentWeekDayIndexForExpense] += t.getAmount();
						}

						previousExpenseDate = t.getDateTime();
					}


					container.addView(rowView);

				} else { // loading for lists in other places e.g. piechart dialog

					if (container_layout != null) {

						if (!t.getDateTime().equals(previousDate)) {
							tDate.setVisibility(View.VISIBLE);
							tDate.setText(dateText);
							if (i > 0) {
								container_layout.addView(getLayoutInflater().inflate(R.layout.x_line, null));
							}
							previousDate = t.getDateTime();
						}

						container_layout.addView(rowView);
					}

				}

			}

		}
	}

}
