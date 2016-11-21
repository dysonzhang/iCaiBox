package com.icaihe.activity_fragment;

import java.util.List;

import com.icaihe.R;
import com.icaihe.adapter.ContractAdapter;
import com.icaihe.application.ICHApplication;
import com.icaihe.model.Contract;
import com.ichihe.util.HttpRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import zuo.biao.library.base.BaseHttpListFragment;
import zuo.biao.library.base.BaseModel;
import zuo.biao.library.interfaces.AdapterCallBack;
import zuo.biao.library.interfaces.CacheCallBack;
import zuo.biao.library.manager.HttpManager.OnHttpResponseListener;
import zuo.biao.library.util.Json;

/**
 * 通讯录界面fragment
 * 
 */
public class ContractListFragment extends BaseHttpListFragment<Contract, ContractAdapter>
		implements OnItemClickListener, OnClickListener, CacheCallBack<Contract> {

	public static ContractListFragment createInstance() {
		ContractListFragment fragment = new ContractListFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState, R.layout.contract_fragment);
		argument = getArguments();
		if (argument != null) {
		}
		initCache(this);

		initView();
		initData();
		initEvent();

		lvBaseList.onRefresh();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		lvBaseList.onRefresh();
	}

	@Override
	public void initView() {
		super.initView();
	}

	@Override
	public void setList(final List<Contract> list) {
		setList(list, new AdapterCallBack<ContractAdapter>() {
			@Override
			public void refreshAdapter() {
				adapter.refresh(list);
			}

			@Override
			public ContractAdapter createAdapter() {
				return new ContractAdapter(context, list);
			}
		});
	}

	@Override
	public void initData() {
		super.initData();
	}

	@Override
	public void getListAsync(int pageNum) {

		if (pageNum != 1) {
			onStopLoadMore(false);
			stopLoadData();
			return;
		}
		long groupId = ICHApplication.getInstance().getCurrentUser().getGroupId();
		HttpRequest.getGroupMemberList(groupId, HttpRequest.RESULT_GET_GROUP_MENBER_SUCCEED,
				new OnHttpResponseListener() {

					@Override
					public void onHttpRequestSuccess(int requestCode, int resultCode, String resultMessage,
							String resultData) {
						// showShortToast("加载成功");
						// list = Json.parseArray(resultData, Contract.class);
						// onLoadSucceed(list);
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
		List<Contract> contractList = Json.parseArray(resultData, Contract.class);
		if (contractList.size() <= 0) {
			showShortToast("当前暂无通讯录信息");
			stopLoadData();
		} else {
			onHttpRequestSuccess(requestCode, resultCode, resultMessage, resultData);
			onStopLoadMore(false);
			stopLoadData();
		}
	}

	@Override
	public List<Contract> parseArray(String json) {
		return Json.parseArray(json, Contract.class);
	}

	@Override
	public Class<Contract> getCacheClass() {
		return Contract.class;
	}

	@Override
	public String getCacheGroup() {
		// return "range=" + range;
		return "";
	}

	@Override
	public String getCacheId(Contract data) {
		return data == null ? null : "" + data.getId();
	}

	@Override
	public void initEvent() {// 必须调用
		super.initEvent();

		lvBaseList.setOnItemClickListener(this);
	}

	// 系统自带监听方法 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		position -= lvBaseList.getHeaderViewsCount();
		if (position < 0 || adapter == null || position >= adapter.getCount()) {
			return;
		}

		Contract contract = adapter.getItem(position);
		if (BaseModel.isCorrect(contract)) {// 相当于 user != null && user.getId()
											// >
											// 0
			// toActivity(UserActivity.createIntent(context, user.getId()));
		}
	}

	@Override
	public void onClick(View v) {

	}
}