/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.malinskiy.hcalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

public class Calculator extends Activity implements PanelSwitcher.Listener, Logic.Listener,
		OnClickListener, OnMenuItemClickListener {
	static final         int     BASIC_PANEL        = 0;
	static final         int     ADVANCED_PANEL     = 1;
	private static final String  LOG_TAG            = "Calculator";
	private static final boolean DEBUG              = false;
	private static final boolean LOG_ENABLED        = false;
	private static final String  STATE_CURRENT_VIEW = "state-current-view";
	EventListener mListener = new EventListener();
	private CalculatorDisplay mDisplay;
	private Persist           mPersist;
	private History           mHistory;
	private Logic             mLogic;
	private ViewPager         mPager;
	private View              mClearButton;
	private View              mBackspaceButton;
	private View              mOverflowMenuButton;
	private GenerateKeysTask  generateKeysTask;

	static void log(String message) {
		if (LOG_ENABLED) {
			Log.v(LOG_TAG, message);
		}
	}

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);

		// Disable IME for this application
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
		                     WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		setContentView(R.layout.main);
		mPager = (ViewPager) findViewById(R.id.panelswitch);
		if (mPager != null) {
			mPager.setAdapter(new PageAdapter(mPager));
		} else {
			// Single page UI
			final TypedArray buttons = getResources().obtainTypedArray(R.array.buttons);
			for (int i = 0; i < buttons.length(); i++) {
				setOnClickListener(null, buttons.getResourceId(i, 0));
			}
			buttons.recycle();
		}

		if (mClearButton == null) {
			mClearButton = findViewById(R.id.clear);
			mClearButton.setOnClickListener(mListener);
			mClearButton.setOnLongClickListener(mListener);
		}
		if (mBackspaceButton == null) {
			mBackspaceButton = findViewById(R.id.del);
			mBackspaceButton.setOnClickListener(mListener);
			mBackspaceButton.setOnLongClickListener(mListener);
		}

		mPersist = new Persist(this);
		mPersist.load();

		mHistory = mPersist.history;

		mDisplay = (CalculatorDisplay) findViewById(R.id.display);

		mLogic = new Logic(this, mHistory, mDisplay);
		mLogic.setListener(this);

		mLogic.setDeleteMode(mPersist.getDeleteMode());
		mLogic.setLineLength(mDisplay.getMaxDigits());

		HistoryAdapter historyAdapter = new HistoryAdapter(this, mHistory, mLogic);
		mHistory.setObserver(historyAdapter);

		if (mPager != null) {
			mPager.setCurrentItem(state == null ? 0 : state.getInt(STATE_CURRENT_VIEW, 0));
		}

		mListener.setHandler(mLogic, mPager, this);
		mDisplay.setOnKeyListener(mListener);

		if (!ViewConfiguration.get(this).hasPermanentMenuKey()) {
			createFakeMenu();
		}

		mLogic.resumeWithHistory();
		if(!Scarab.isInitialized) {
			Scarab.isInitialized = true;
			new GenerateKeysTask(this).execute();
		}
		updateDeleteMode();
	}

	@Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		if (mPager != null) {
			state.putInt(STATE_CURRENT_VIEW, mPager.getCurrentItem());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mLogic.updateHistory();
		mPersist.setDeleteMode(mLogic.getDeleteMode());
		mPersist.save();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyCode == KeyEvent.KEYCODE_BACK && getAdvancedVisibility()
				&& mPager != null) {
			mPager.setCurrentItem(BASIC_PANEL);
			return true;
		} else {
			return super.onKeyDown(keyCode, keyEvent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.basic).setVisible(!getBasicVisibility());
		menu.findItem(R.id.advanced).setVisible(!getAdvancedVisibility());
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.clear_history:
				mHistory.clear();
				mLogic.onClear();
				break;

			case R.id.basic:
				if (!getBasicVisibility() && mPager != null) {
					mPager.setCurrentItem(BASIC_PANEL, true);
				}
				break;

			case R.id.advanced:
				if (!getAdvancedVisibility() && mPager != null) {
					mPager.setCurrentItem(ADVANCED_PANEL, true);
				}
				break;

			case R.id.test_integrity:
				new TestTask(this).execute();
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateDeleteMode() {
		if (mLogic.getDeleteMode() == Logic.DELETE_MODE_BACKSPACE) {
			mClearButton.setVisibility(View.GONE);
			mBackspaceButton.setVisibility(View.VISIBLE);
		} else {
			mClearButton.setVisibility(View.VISIBLE);
			mBackspaceButton.setVisibility(View.GONE);
		}
	}

	void setOnClickListener(View root, int id) {
		final View target = root != null ? root.findViewById(id) : findViewById(id);
		target.setOnClickListener(mListener);
	}

	private void createFakeMenu() {
		mOverflowMenuButton = findViewById(R.id.overflow_menu);
		if (mOverflowMenuButton != null) {
			mOverflowMenuButton.setVisibility(View.VISIBLE);
			mOverflowMenuButton.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.overflow_menu:
				PopupMenu menu = constructPopupMenu();
				if (menu != null) {
					menu.show();
				}
				break;
		}
	}

	private PopupMenu constructPopupMenu() {
		final PopupMenu popupMenu = new PopupMenu(this, mOverflowMenuButton);
		final Menu menu = popupMenu.getMenu();
		popupMenu.inflate(R.menu.menu);
		popupMenu.setOnMenuItemClickListener(this);
		onPrepareOptionsMenu(menu);
		return popupMenu;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return onOptionsItemSelected(item);
	}

	private boolean getBasicVisibility() {
		return mPager != null && mPager.getCurrentItem() == BASIC_PANEL;
	}

	private boolean getAdvancedVisibility() {
		return mPager != null && mPager.getCurrentItem() == ADVANCED_PANEL;
	}

	@Override
	public void onChange() {
		invalidateOptionsMenu();
	}

	@Override
	public void onDeleteModeChange() {
		updateDeleteMode();
	}

	private void setDialogProgress(final ProgressDialog progressDialog, final String progress) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressDialog.setMessage(progress);
			}
		});
	}

	private enum GENERATE_PROGRESS {
		KEY_GENERATION,
		GET_SECRET_KEY,
		GET_PUBLIC_KEY
	}

	private enum TEST_PROGRESS {
		XOR_TESTING,
		AND_TESTING,
		ADD_TESTING,
		SUB_TESTING,
		MUL_TESTING,
		DIV_TESTING
	}

	private static class GenerateKeysTask extends AsyncTask<Void, GENERATE_PROGRESS, String[]> {
		private ProgressDialog    mDialog;
		private GENERATE_PROGRESS progress;
		private Calculator        mActivity;

		public GenerateKeysTask(Calculator calculator) {
			super();
			mActivity = calculator;
		}


		private void setProgress(GENERATE_PROGRESS progress) {
			this.progress = progress;
			onProgressUpdate(progress);
		}

		@Override
		protected String[] doInBackground(Void... voids) {
			setProgress(GENERATE_PROGRESS.KEY_GENERATION);
			Scarab.generateKeys();
			setProgress(GENERATE_PROGRESS.GET_SECRET_KEY);
			String secretKey = Scarab.getSecretKey();
			setProgress(GENERATE_PROGRESS.GET_PUBLIC_KEY);
			String publicKey = Scarab.getPublicKey();

			String result[] = {secretKey, publicKey};
			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = new ProgressDialog(mActivity);
			mDialog.setTitle("Initialization");
			mDialog.setCancelable(false);
			mDialog.show();
		}

		@Override
		protected void onProgressUpdate(final GENERATE_PROGRESS... values) {
			super.onProgressUpdate(values);
			if (values != null & values.length == 1 && mActivity != null) {
				switch (values[0]) {
					case KEY_GENERATION:
						mActivity.setDialogProgress(mDialog, "Generating keypair");
						break;
					case GET_SECRET_KEY:
						mActivity.setDialogProgress(mDialog, "Receiving secret key");
						break;
					case GET_PUBLIC_KEY:
						mActivity.setDialogProgress(mDialog, "Receiving public key");
						break;
				}
			}
		}

		@Override
		protected void onPostExecute(String[] strings) {
			super.onPostExecute(strings);
			if (mDialog.isShowing()) {
				mDialog.dismiss();
			}
		}


	}

	class PageAdapter extends PagerAdapter {
		private View mSimplePage;
		//private View mAdvancedPage;

		public PageAdapter(ViewPager parent) {
			final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			final View simplePage = inflater.inflate(R.layout.simple_pad, parent, false);
			//final View advancedPage = inflater.inflate(R.layout.advanced_pad, parent, false);
			mSimplePage = simplePage;
			//mAdvancedPage = advancedPage;

			final Resources res = getResources();
			final TypedArray simpleButtons = res.obtainTypedArray(R.array.simple_buttons);
			for (int i = 0; i < simpleButtons.length(); i++) {
				setOnClickListener(simplePage, simpleButtons.getResourceId(i, 0));
			}
			simpleButtons.recycle();

//            final TypedArray advancedButtons = res.obtainTypedArray(R.array.advanced_buttons);
//            for (int i = 0; i < advancedButtons.length(); i++) {
//                setOnClickListener(advancedPage, advancedButtons.getResourceId(i, 0));
//            }
//            advancedButtons.recycle();

			final View clearButton = simplePage.findViewById(R.id.clear);
			if (clearButton != null) {
				mClearButton = clearButton;
			}

			final View backspaceButton = simplePage.findViewById(R.id.del);
			if (backspaceButton != null) {
				mBackspaceButton = backspaceButton;
			}
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			//final View page = position == 0 ? mSimplePage : mAdvancedPage;
			final View page = mSimplePage;
			container.addView(page);
			return page;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}
	}

	private class TestTask extends AsyncTask<Void, TEST_PROGRESS, TestResults> {
		private final int TEST_COUNT_BIT_OPS   = 16;
		private final int TEST_COUNT_ARITH_OPS = 2;
		private ProgressDialog mDialog;
		private Context        mContext;

		public TestTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = new ProgressDialog(mContext);
			mDialog.setTitle("Testing scarab integrity");
			mDialog.setCancelable(false);
			mDialog.show();
		}

		@Override
		protected TestResults doInBackground(Void... voids) {
			onProgressUpdate(TEST_PROGRESS.AND_TESTING);
			int andSuccessed = 0;
			for (int i = 0; i < TEST_COUNT_BIT_OPS; i++) {
				if (Scarab.testAnd())
					andSuccessed++;
			}
			onProgressUpdate(TEST_PROGRESS.XOR_TESTING);
			int xorSuccessed = 0;
			for (int i = 0; i < TEST_COUNT_BIT_OPS; i++) {
				if (Scarab.testXor())
					xorSuccessed++;
			}
			int addSuccessed = 0;
			onProgressUpdate(TEST_PROGRESS.ADD_TESTING);
			for (int i = 0; i < TEST_COUNT_ARITH_OPS; i++) {
				if (Scarab.testAdd()) {
					addSuccessed++;
				}
			}
			int subSuccessed = 0;
			onProgressUpdate(TEST_PROGRESS.SUB_TESTING);
			for (int i = 0; i < TEST_COUNT_ARITH_OPS; i++) {
				if (Scarab.testSub()) {
					subSuccessed++;
				}
			}

			return new TestResults(xorSuccessed, andSuccessed, addSuccessed, subSuccessed);
		}

		@Override
		protected void onProgressUpdate(final TEST_PROGRESS... values) {
			super.onProgressUpdate(values);
			if (values != null & values.length == 1) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						switch (values[0]) {
							case AND_TESTING:
								mDialog.setMessage("Testing conjunction");
								break;
							case XOR_TESTING:
								mDialog.setMessage("Testing adding modulo 2");
								break;
							case ADD_TESTING:
								mDialog.setMessage("Testing integer addition");
								break;
							case SUB_TESTING:
								mDialog.setMessage("Testing integer subtraction");
								break;
							case MUL_TESTING:
								mDialog.setMessage("Testing integer multiplication");
								break;
							case DIV_TESTING:
								mDialog.setMessage("Testing integer division");
								break;
							default:
								mDialog.setMessage("Unknown test in progress");
								break;
						}
					}
				});
			}
		}

		@Override
		protected void onPostExecute(final TestResults values) {
			super.onPostExecute(values);
			if (values != null) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mDialog.dismiss();
						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						builder.setTitle("Results");
						View dialogView = getLayoutInflater().inflate(R.layout.testresults, null);
						builder.setView(dialogView);
						builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						AlertDialog resultDialog = builder.show();
						TextView xor = (TextView) resultDialog.findViewById(R.id.test_xor_value);
						TextView and = (TextView) resultDialog.findViewById(R.id.test_and_value);
						TextView add = (TextView) resultDialog.findViewById(R.id.test_add_value);
						TextView sub = (TextView) resultDialog.findViewById(R.id.test_sub_value);
						xor.setText(String.valueOf(values.getXor()) + "/" + TEST_COUNT_BIT_OPS);
						and.setText(String.valueOf(values.getAnd()) + "/" + TEST_COUNT_BIT_OPS);
						add.setText(String.valueOf(values.getAdd()) + "/" + TEST_COUNT_ARITH_OPS);
						sub.setText(String.valueOf(values.getSub()) + "/" + TEST_COUNT_ARITH_OPS);
					}
				});
			}
			;

		}
	}

	private class TestResults {
		private int xor;
		private int and;
		private int add;
		private int sub;

		private TestResults(int xor, int and, int add, int sub) {
			this.xor = xor;
			this.and = and;
			this.add = add;
			this.sub = sub;
		}

		private int getXor() {
			return xor;
		}

		private int getAnd() {
			return and;
		}

		private int getAdd() {
			return add;
		}

		private int getSub() {
			return sub;
		}
	}
}
