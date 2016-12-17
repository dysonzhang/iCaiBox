package com.icaihe.activity_fragment;

import java.util.List;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.icaihe.R;
import com.icaihe.adapter.AlarmNoticeAdapter;
import com.icaihe.manager.DataManager;
import com.icaihe.model.AlarmNotice;
import com.ichihe.util.HttpRequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import zuo.biao.library.base.BaseHttpListActivity;
import zuo.biao.library.base.BaseModel;
import zuo.biao.library.interfaces.AdapterCallBack;
import zuo.biao.library.interfaces.OnBottomDragListener;
import zuo.biao.library.manager.HttpManager.OnHttpResponseListener;
import zuo.biao.library.util.Json;

public class ActivityAlarmNotice extends BaseHttpListActivity<AlarmNotice, AlarmNoticeAdapter>
		implements OnClickListener, OnItemClickListener, OnLongClickListener, OnBottomDragListener {

	public static Intent createIntent(Context context) {
		return new Intent(context, ActivityAlarmNotice.class);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_notice, this);

		initView();
		initData();
		initEvent();

		SVProgressHUD.showWithStatus(context, "请稍候...");
		lvBaseList.onRefresh();
		SVProgressHUD.dismiss(context);
	}
	@Override
	public void toWarnActivity(boolean isBattery) {
		Intent intent = ActivityWran.createIntent(context);
		intent.putExtra("isBattery", isBattery ? 1 : 0);
		toActivity(intent);
	}
	private ImageView iv_back;

	@Override
	public void initView() {
		super.initView();
		iv_back = (ImageView) findViewById(R.id.iv_back);
	}

	@Override
	public void initData() {
		super.initData();
	}

	@Override
	public void initEvent() {
		super.initEvent();

		iv_back.setOnClickListener(this);
		lvBaseList.setOnItemClickListener(this);
	}

	@Override
	public void onDragBottom(boolean rightToLeft) {
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	@Override
	public List<AlarmNotice> parseArray(String json) {
		return Json.parseArray(json, AlarmNotice.class);
	}

	@Override
	public void setList(final List<AlarmNotice> list) {
		setList(list, new AdapterCallBack<AlarmNoticeAdapter>() {
			@Override
			public void refreshAdapter() {
				adapter.refresh(list);
			}

			@Override
			public AlarmNoticeAdapter createAdapter() {
				return new AlarmNoticeAdapter(context, list);
			}
		});
	}

	@Override
	public void getListAsync(int pageNum) {
		int boxId = (int) DataManager.getInstance().getCurrentUser().getBoxId();
		if (boxId == 0) {
			showShortToast("当前财盒ID数据错误");
			return;
		}

		HttpRequest.queryAlarmRecords(boxId, pageNum, HttpRequest.RESULT_QUERY_ALARM_RECORDS_SUCCEED,
				new OnHttpResponseListener() {
					@Override
					public void onHttpRequestSuccess(int requestCode, int resultCode, String resultMessage,
							String resultData) {
						if (resultCode != 1) {
							showShortToast("requestCode->" + requestCode + " resultMessage->" + resultMessage);
							return;
						}
						setOnHttpRequestSuccess(requestCode, resultCode, resultMessage, resultData);
					}

					@Override
					public void onHttpRequestError(int requestCode, String resultMessage, Exception exception) {
						showShortToast("onHttpRequestError " + "requestCode->" + requestCode + " resultMessage->"
								+ resultMessage);
					}
				});
	}

	private void setOnHttpRequestSuccess(int requestCode, int resultCode, String resultMessage, String resultData) {
		if (resultData.equals("null")) {
			showShortToast("报警记录为空");
			return;
		}

		String results = Json.parseObject(resultData).getString("results");
		List<AlarmNotice> alarmList = Json.parseArray(results, AlarmNotice.class);

		if (alarmList != null && alarmList.size() > 0) {
			onHttpRequestSuccess(requestCode, resultCode, resultMessage, results);
		} else {
			stopLoadData();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		position -= lvBaseList.getHeaderViewsCount();
		if (position < 0 || adapter == null || position >= adapter.getCount()) {
			return;
		}

		AlarmNotice alarmNotice = adapter.getItem(position);
		if (BaseModel.isCorrect(alarmNotice)) {// 相当于 user != null &&
												// user.getId() >
												// 0
			// toActivity(UserActivity.createIntent(context, user.getId()));
		}
	}
}
