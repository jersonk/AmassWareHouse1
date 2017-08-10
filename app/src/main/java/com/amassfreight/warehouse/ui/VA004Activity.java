package com.amassfreight.warehouse.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.SelectDict;
import com.amassfreight.domain.VA004Data;
import com.amassfreight.domain.VATVasExpensesDt;
import com.amassfreight.domain.VasVA004;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.ui.dialogs.OnDialogOkListener;
import com.google.gson.reflect.TypeToken;

public class VA004Activity extends BaseActivity implements OnDialogOkListener {

	private ListView listVas;
	private LayoutInflater mInflater;
	private ArrayAdapter vasAdapter;
	private String strCdOrder; // 进仓编号
	private String strServiceId; // 增值服务ID
	private String strFlgDefault; // 缺省科目
	private Button btn_Add_Vas;
	private Button btn_Approval;
	private int flgAppoal;
	private Boolean flgSupervisor; //主管标志
	// 添加耗材弹出画面 add by yxq 2014/05/21 begin
	private Spinner spVas; 		        // 尚未添加的耗材
	private String 	strVasId;           // 耗材Id
	private String 	strVasName;         // 耗材Name
	private double 	douVasNum;          // 耗材数量
	private double	douVasWeight;		// 耗材重量
	private double	douVasTotalWeight;	// 耗材总重量
	private boolean flgWeight;			// 必选输入重量
	private Integer intVasBelong;       // 耗材所属
	private String 	strVasExpensesDtId; // 增值服务费用明细ID
	private boolean isCkbSelectValid;   // 选择控件变化是否有效
	private RadioButton radioButton;    // 耗材所属单选框
	private RadioGroup radioGroup;      // 耗材所属单选框组
	private RadioGroup.LayoutParams radiolayout; // 单选框的样式
	// 添加耗材弹出画面 add by yxq 2014/05/21 end

	private List<SelectDict> vasSumList;   // 耗材的计算方式   add by yxq 2014/09/23
	// add by yxq 2014/09/26 begin
	private LinearLayout linLay;    
	private int materialNum;               //耗材数量
	private double materialCbm;            //耗材体积
	private double materialWeight;            //耗材体积
	private EditText editTextNum;
	private EditText editTextWeight;
	private EditText editTextTotalWeight;
	private List<VasVA004> vasDataList;
	
	// add by yxq 2014/09/26 end
	/** 初始化事件 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		strCdOrder = (String) intent.getSerializableExtra("strCdOrder");
		strServiceId = (String) intent.getSerializableExtra("strServiceId");;
		strFlgDefault = (String) intent.getSerializableExtra("strFlgDefault");
		materialNum = intent.getIntExtra("materialNum", 0);
		materialCbm = intent.getDoubleExtra("materialCbm", 0);
		materialWeight = intent.getDoubleExtra("materialWeight", 0);
		setTitle(getString(R.string.title_activity_VA004));
		super.onCreate(savedInstanceState);

		setContentView(R.layout.va004_list_view);

		setupActionBar();
		
		btn_Add_Vas = (Button) findViewById(R.id.btn_Add_Vas);

		btn_Approval = (Button) findViewById(R.id.btn_Approval);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		listVas = (ListView) findViewById(R.id.listVas);

		vasDataList = new ArrayList<VasVA004>();
		
		isCkbSelectValid = true;  // 选择控件值变换有效
		
		// 单选框样式
		int sWrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
		radiolayout =  new RadioGroup.LayoutParams(sWrapContent,sWrapContent);
		radiolayout.setMargins(50, 0,50, 0);

		// 适配器
		vasAdapter = new ArrayAdapter(getApplicationContext(), 0// R.layout.sc_list_view_item
		) {
			public View getView(int position, View convertView, ViewGroup parent) {
				RelativeLayout layout = new RelativeLayout(getContext());
				VasVA004 data = (VasVA004) getItem(position);
				try {
					VasVA004 item = (VasVA004) data;
					int intColor = 0;
					if (item.getVasdefault()) {
						intColor = 0xFFFF8000;   // 棕色
					}else{
						intColor = android.graphics.Color.BLACK;
					}
					// 设置背景颜色
					if (position % 2 == 0) {
						layout.setBackgroundResource(R.color.listview_back_odd);
					} else {
						layout.setBackgroundResource(R.color.listview_back_uneven);
					}
					mInflater.inflate(R.layout.va004_list_view_item, layout,true);
					TextView  t1 = (TextView) layout.findViewById(R.id.txtVasExpDtId);
					t1.setText(item.getVasExpensesDtId());
					t1.setVisibility(View.GONE);
					
					t1 = (TextView) layout.findViewById(R.id.txtVasNmChn);
					t1.setText(item.getVasNmChn());// 名称
					t1.setTextColor(intColor);     // 颜色
					t1 = (TextView) layout.findViewById(R.id.txtQuantityName); // 文字:"数量："
					t1 = (TextView) layout.findViewById(R.id.txtQuantity);
					// 去掉 为"0"的小数位
					String strNum = item.getQuantity().toString();
					String strNumList[] = strNum.split("\\.");
					if(strNumList.length > 1 && "0".equals(strNumList[1])){
						strNum = strNum.replace(".0","");
					}
					t1.setText(strNum);// 数量
					final int selectPosition = position;

					// 获取选择控件
					CheckBox ckbSelect = (CheckBox) layout.findViewById(R.id.ckbIsSelect);
					ckbSelect.setChecked(item.getIsSelect());
					// 判断是否已经提交审核 modify by sdhuang 2014-12-29
					if(item.getFlgAppoval() > 1){
						ckbSelect.setVisibility(View.GONE);
					}else if(item.getFlgAppoval() == 1){
						if(flgSupervisor){
							ckbSelect.setVisibility(View.VISIBLE);
						}else{
							ckbSelect.setVisibility(View.GONE);
						}
					}else{
						ckbSelect.setVisibility(View.VISIBLE);
					}
					/*if (item.getFlgAppoval() != 0) {
						ckbSelect.setVisibility(View.GONE);
					}*/

					// checkBox的变化事件
					ckbSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(final CompoundButton arg0, final boolean arg1) {
							if(!isCkbSelectValid){  // 无效
								isCkbSelectValid = true;
								return;
							}
							String strMessage = "";
							if(arg1){	
								//strMessage = getString(R.string.VA004_007_MSG);
								ShowInputDialig("update",selectPosition,arg0);
								return;
							}else{
								// 获取当前选择项
								VasVA004 mData = (VasVA004) listVas.getItemAtPosition(selectPosition);
								strMessage = String.format(
											getString(R.string.VA004_008_MSG),mData.getVasNmChn());
							}
							new AlertDialog.Builder(new ContextThemeWrapper(_thisActivity,
									android.R.style.Theme_Holo_Light))
							.setIcon(R.drawable.ic_launcher)
							.setTitle(R.string.app_name)
							.setMessage(strMessage)
							.setPositiveButton(R.string.button_ok,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int whichButton) {
											// 获取当前选择项
											VasVA004 mData = (VasVA004) listVas.getItemAtPosition(selectPosition);
											VasVA004 vs004 = (VasVA004) mData;
										    if(arg1){
										    	//DoWithVasExpenses(vs004,"add");
											}else{
												DoWithVasExpenses(vs004,"delete");
											}
										}
									})
							.setCancelable(false)
							.setNegativeButton(R.string.button_no,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,int whichButton) {
											isCkbSelectValid = false ; // 无效
											arg0.setChecked(!arg0.isChecked());  
										}
									}).show();
						}
					});
				} catch (Exception e) {
					try {
						throw e;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				return layout;
			}

		};

		listVas.setAdapter(vasAdapter);

		GetScData(vasDataList);

		// listview选择监听事件
		listVas.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(0 == flgAppoal){   // 尚未审核
					ShowInputDialig("update",arg2,null);
				}
			}
		});

		// 添加耗材事件
		btn_Add_Vas.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// add by yxq 2014/05/20 begin
				ShowInputDialig("add",0,null);     // 弹出输入耗材和数量的对话框(添加模式)
				// add by yxq 2014/05/20 end
			}
		});

		// 提交审核事件
		btn_Approval.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 你确定要审核申请吗？
				showDoubleDialog(_thisActivity, getString(R.string.VA004_006_MSG), 0);
			}
		});
	}

	/**
	 * Dialog(YES/NO)
	 * 
	 */
	public void showDoubleDialog(Context context, String str, final int dialogId) {
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				android.R.style.Theme_Holo_Light))
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.app_name)
				.setMessage(str)
				.setPositiveButton(R.string.button_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// DoubleDialog(yes/no)
								actionAfterOk(dialogId, 0);
							}
						})
				.setCancelable(false)
				.setNegativeButton(R.string.button_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// DoubleDialog(yes/no)
								actionAfterOk(dialogId, 1);

							}
						}).show();
	}

	/**
	 * DoubleDialog(yes/no)
	 * 
	 * @param dialogId
	 *            “0”代表确定，“1”代表取消
	 */
	public void actionAfterOk(int dialogId, int i) {

		// dialogId==0 系统MEN选中
		if (dialogId == 0) {
			// 标识
			if (i == 0) {
				Map<String, Object> tvasDtMap = new HashMap<String, Object>();
				tvasDtMap.put("serviceId", strServiceId);// 服务ID

				NetworkHelper.getInstance().postJsonData(_thisActivity,
						"VA004_DoSave", tvasDtMap, VasVA004.class,
						new AmassHttpResponseHandler<VasVA004>() {
							@Override
							protected void OnSuccess(VasVA004 response) {
								super.OnSuccess(response);
								flgAppoal = 1;  // 已经审核
								refresh();      // 刷新画面
							}

						}, true);
			}
		}
	}

	/* 获取数据加载 */
	private void GetScData(final List<VasVA004> vasDataList) {

		Map<String, Object> tvasDtMap = new HashMap<String, Object>();

		tvasDtMap.put("serviceId", strServiceId);// 增值服务ID
		tvasDtMap.put("vasType", strFlgDefault);// 服务类型

		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"VA004_GetVasList", tvasDtMap, new TypeToken<VA004Data>() {
				}.getType(), new AmassHttpResponseHandler<VA004Data>() {
					@Override
					protected void OnSuccess(VA004Data response) {
						super.OnSuccess(response);
						VA004Data va004Data = (VA004Data) response;
						flgAppoal = va004Data.getFlgAppoval();
						flgSupervisor = va004Data.getFlgSupervisor();
						btn_Add_Vas.setVisibility(View.VISIBLE);
						btn_Approval.setVisibility(View.VISIBLE);
                        // modify by sdhuang 2014-12-29
						// 已审核不能修改耗材
						if(flgAppoal > 1){
							btn_Add_Vas.setEnabled(false);
							btn_Approval.setEnabled(false);
						}else if(flgAppoal == 1){
							// 提交审核，但未审核
							if(va004Data.getFlgSupervisor()){
								btn_Add_Vas.setEnabled(true);
								btn_Approval.setEnabled(false);
							}else{
								btn_Add_Vas.setEnabled(false);
								btn_Approval.setEnabled(false);
							}
						}else{
							btn_Add_Vas.setEnabled(true);
							btn_Approval.setEnabled(true);
						}
						
						List<VasVA004> ms = va004Data.getVasVa004List();
						vasSumList = va004Data.getVasSumList();  // add yxq 2014/09/23
						vasAdapter.clear();
						vasAdapter.addAll(ms);
						vasAdapter.notifyDataSetChanged();
					}

				}, true);

	}

	@Override
	public void onDialogResult(int requestCode, int resultCode, Intent data) {

	}
	
	// add by yxq 2014/05/20  begin
	// 添加耗材输入弹出框
	// inputStyleFlag   1. "add":添加模式   2.  "update":修改模式
	public void ShowInputDialig(final String inputStyleFlag,
			final int position,final CompoundButton button){

		AlertDialog.Builder builder = new AlertDialog.Builder(this).setCancelable(false);
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.va004_list_view_input_dialog, null);
		editTextNum = (EditText)textEntryView.findViewById(R.id.editTextNum);
		editTextWeight = (EditText)textEntryView.findViewById(R.id.editTextWeight);
		editTextTotalWeight = (EditText)textEntryView.findViewById(R.id.editTextTotalWeight);
		editTextWeight.setSelectAllOnFocus(true);
		editTextNum.setSelectAllOnFocus(true);  // 全选
		
		if("add".equals(inputStyleFlag)){  	 // 添加模式
			strVasExpensesDtId = null;
			intVasBelong = 0;
		}else if("update".equals(inputStyleFlag)){    // 修改数量
			// 选中项
			VasVA004 mData = (VasVA004) listVas.getItemAtPosition(position);
			strVasId  = mData.getVasId();
			strVasName   = mData.getVasNmChn();
			strVasExpensesDtId = mData.getVasExpensesDtId();
			douVasNum = mData.getQuantity();
			intVasBelong = mData.getVasBelong();
			douVasWeight = mData.getWeight();
			flgWeight = mData.getFlgWeight();
			
			if (strVasName.equals("定制托")){
				editTextWeight.setEnabled(true);
			}
			else{
				editTextWeight.setEnabled(false);
			}
		}

		
		editTextNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				Double num = Double.parseDouble(editTextNum.getText().toString().trim());
				Double weight = Double.parseDouble(editTextWeight.getText().toString().trim());
				Double totalWeight = num * weight;
				editTextTotalWeight.setText(String.valueOf(totalWeight));				
			}
		});
		
		editTextWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				Double num = Double.parseDouble(editTextNum.getText().toString().trim());
				Double weight = Double.parseDouble(editTextWeight.getText().toString().trim());
				Double totalWeight = num * weight;
				editTextTotalWeight.setText(String.valueOf(totalWeight));
			}
		});
		
		
		spVas = (Spinner) textEntryView.findViewById(R.id.sp_Vas);                // 耗材名称下拉框
		// add by yxq 2014/09/26 begin
		linLay = (LinearLayout) textEntryView.findViewById(R.id.linLay);
		// add by yxq 2014/09/26 end
		// add by yxq 2014/09/23
		spVas.setOnItemSelectedListener(new OnItemSelectedListener(){
	        public void  onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
	        	editTextNum.setText(GetNum(((SelectDict) spVas.getSelectedItem()).getId()));
	        	Double weight = ((SelectDict) spVas.getSelectedItem()).getNumProperty();
	        	flgWeight = ((SelectDict) spVas.getSelectedItem()).getFlgProperty() == null ? false : ((SelectDict) spVas.getSelectedItem()).getFlgProperty();
	        	editTextWeight.setText(weight.toString());
	        	// add by yxq 2014/09/26 begin
	        	// 根据选择的耗材显示
	        	DescBySpVas();
	        	// add by yxq 2014/09/26 end
	        	//editTextWeight.setText(String.valueOf(douVasWeight));
				Double num = Double.parseDouble(editTextNum.getText().toString().trim());
				
				Double totalWeight = num * weight;	        	
	        	editTextTotalWeight.setText(String.valueOf(totalWeight));
				if (((SelectDict) spVas.getSelectedItem()).getName().equals("定制托")){
					editTextWeight.setEnabled(true);
				}
				else{
					editTextWeight.setEnabled(false);
				}
	        }
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		// add by yxq 2014/09/23
		// radioButton = (RadioButton) textEntryView.findViewById(R.id.radioButton); // 耗材所属
		radioGroup = (RadioGroup) textEntryView.findViewById(R.id.radioGroup); // 耗材所属
		GetVasBelong();  // 耗材所属单选框
		// 添加耗材时，显示的项目
		TextView txtPopMvsName_ins = (TextView)textEntryView.findViewById(R.id.txtPopMvsName_ins); 
		// 修改耗材时，显示的项目
		TextView txtPopMvsName_upd = (TextView)textEntryView.findViewById(R.id.txtPopMvsName_upd); 
		if("add".equals(inputStyleFlag)){  			  // 添加模式
			builder.setTitle(getString(R.string.VA004_Title_Add_Mvs)); 
			txtPopMvsName_upd.setVisibility(View.GONE);
			InitMvsList();     						  // 初始化耗材下拉框
		}else if("update".equals(inputStyleFlag)){    // 修改数量
			builder.setTitle(strVasName);
			spVas.setVisibility(View.GONE);
			txtPopMvsName_ins.setVisibility(View.GONE);
			// add by yxq 2014/09/28 begin
		    if("ID_NONE".equals(strVasId)){
		    	linLay.setVisibility(View.GONE);
		    }else{
			// add by yxq 2014/09/28 end
				// 赋值原本的数量，去除为"0"的小数位
				String strNum = String.valueOf(douVasNum);
				String strWeight = String.valueOf(douVasWeight);
				
				String strNumList[] = strNum.split("\\.");
				if(strNumList.length > 1 && "0".equals(strNumList[1])){
					strNum = strNum.replace(".0","");
				}
				// add by yxq 2014/09/23 begin
				if("0".equals(strNum)){
					strNum = GetNum(strVasId);
				}
				// add by yxq 2014/09/23 end
				editTextNum.setText(strNum);
				editTextWeight.setText(strWeight);
        		douVasNum = Double.parseDouble(strNum);  
        		douVasWeight = Double.parseDouble(strWeight);
				String strTotalWeigh = String.valueOf(douVasWeight * douVasNum);
				editTextTotalWeight.setText(strTotalWeigh);
		    }
		}
		builder.setView(textEntryView);
		final AlertDialog dialog = builder.show();// 显示对话框
		dialog.setCanceledOnTouchOutside(false);  // 点击空白处，不关闭对话框
		// "保存"按钮单击事件
		Button btnSave = (Button) textEntryView.findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
	        	  String strVasNum;
	        	  String strVasWeight;
	        	// add by yxq 2014/09/28 begin
	        	if("ID_NONE".equals(strVasId)) {   // 无耗材
	        		strVasNum = "0";        // 数量设为0
	        		strVasWeight = "0";
	        	}else{
	        	// add by yxq 2014/09/28 end
	        		strVasNum = editTextNum.getText().toString();   // 数量
	        		strVasWeight = editTextWeight.getText().toString();   // 重量
	        	}
	        	if("add".equals(inputStyleFlag)){  			  			  // 添加模式
		        	if(null == spVas.getSelectedItem()){     // 未选择耗材名称
		        		Utils.showAlertDialog(_thisActivity, getString(R.string.VA004_001_MSG)); 
		        		return;
		        	}
		        	SelectDict sd = (SelectDict)spVas.getSelectedItem();  // 添加的耗材
		        	strVasId = sd.getId();
	        	}
	        	
	        	if(strVasNum.isEmpty()){    // 未输入数量
	        		Utils.showAlertDialog(_thisActivity, getString(R.string.VA004_002_MSG)); 
	        		return;
	        	}
	        	//如果选择的是托盘耗材，判断输入的数字是否是整数
	        	for (int i = 0; i < vasSumList.size(); i++){
	        		SelectDict vasSum = vasSumList.get(i);
	        		if (strVasId.equals(vasSum.getId())){
	        			Integer pkgs = (int)Math.floor((Double.parseDouble(strVasNum)));
	        			if (vasSum.getFlgType() == 2 && !pkgs.toString().equals(strVasNum)){
	        				Utils.showAlertDialog(_thisActivity, getString(R.string.VA004_010_MSG));
	        				return;
	        			}
	        			break;
	        		}
	        	}
	        	try{
	        		douVasNum = Double.parseDouble(strVasNum);  
	        		douVasWeight = Double.parseDouble(strVasWeight);
	        	}catch(Exception e){
	        		// 输入的数量不是数字
	        		Utils.showAlertDialog(_thisActivity, getString(R.string.VA004_003_MSG));
	        		return;
	        	}
	        	// edit by yxq 2014/09/28 begin
	        	// 原代码 if(douVasNum <= 0 ){  // 不能小于0
	        	if(!"ID_NONE".equals(strVasId) && douVasNum <= 0 ){  // 不是 无耗材 且 不能小于0
	            // edit by yxq 2014/09/28 end
	        		Utils.showAlertDialog(_thisActivity, getString(R.string.VA004_009_MSG));
	        		return;
	        	}
	        	String[] splitVasNum = strVasNum.split("\\.");
	        	if(splitVasNum[0].length()>6){   // 整数位位数超过6
	        		Utils.showAlertDialog(_thisActivity, getString(R.string.VA004_004_MSG));
	        		return;
	        	}
	        	if(splitVasNum.length>1 && splitVasNum[1].length()>4){  // 小数位位数大于4
	        		Utils.showAlertDialog(_thisActivity, getString(R.string.VA004_005_MSG));
	        		return;
	        	}
	        	// add by yxq 2014/09/28 begin
	        	if("ID_NONE".equals(strVasId)){
	        		intVasBelong = null;
	        	}else{
	        	// add by yxq 2014/09/28 end
	        		intVasBelong = radioGroup.getCheckedRadioButtonId();
	        	}
	        	if (true == flgWeight){
	        		if (strVasWeight == "" || Double.parseDouble(strVasWeight) <= 0){
	        			editTextWeight.requestFocus();
	        			Utils.showAlertDialog(_thisActivity, getString(R.string.VA004_011_MSG));
		        		return;
	        		}
	        	}
	        	addOrUpdateVasExpenses();   // 添加耗材
	          }
	     }); 
		 
		 // "取消"按钮单击事件
	     Button btnCancel = (Button) textEntryView.findViewById(R.id.btn_cancel);
	     btnCancel.setOnClickListener(new View.OnClickListener() {
	    	 public void onClick(View v) {
	    		 if(button != null){        // 点击"取消"按钮时
					isCkbSelectValid = false ; // 无效
					button.setChecked(false); 
	    		 }
	    		 dialog.dismiss();   // 关闭对话框
	         }
	     });  
	}
	
	// 添加的耗材List
	public void InitMvsList(){
		Map<String, Object> tvasDtMap = new HashMap<String, Object>();
		tvasDtMap.put("serviceId", strServiceId);// 增值服务ID
		tvasDtMap.put("vasType", strFlgDefault);// 服务类型
		NetworkHelper.getInstance().postJsonData(_thisActivity,
				"VA004_POP_GetVasList", tvasDtMap, new TypeToken<List<SelectDict>>() {
				}.getType(), new AmassHttpResponseHandler<List<SelectDict>>() {
					@Override
					protected void OnSuccess(List<SelectDict> response) {
						super.OnSuccess(response);
						List<SelectDict> ms = (List<SelectDict>) response;
						ArrayAdapter<SelectDict> selectList = new ArrayAdapter<SelectDict>
																(_thisActivity,android.R.layout.simple_spinner_item, ms);
						selectList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spVas.setAdapter(selectList);
					}
				}, true);
	}
	
	// 耗材所属
	public void GetVasBelong(){
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("cdType", "VAS_ITEM_BELONG");
		NetworkHelper.getInstance().postJsonData(_thisActivity, "Sys_GetDictList", p,
				new TypeToken<List<SelectDict>>(){}.getType(), new AmassHttpResponseHandler<List<SelectDict>>(){
			@Override
			protected void OnSuccess(List<SelectDict> response) {
				super.OnSuccess(response);
				List<SelectDict> list = response;
				for (int i = list.size() - 1;i > -1; i--) {
			        //添加单选按钮
			        radioButton = new RadioButton(_thisActivity);
			        radioButton.setText(list.get(i).getName());
			        radioButton.setId(Integer.parseInt(list.get(i).getId()));
			        radioButton.setTextColor(0xFFFFFFFF);
			        radioButton.setButtonDrawable(R.drawable.radiobutton_style);
			        radioGroup.addView(radioButton,radiolayout);
				}
				if(intVasBelong != null){ 
					radioGroup.check(intVasBelong);
				}
			}
			
		}, false);
	}
	
	// 添加耗材
	public void addOrUpdateVasExpenses(){
		Map<String, Object> tvasDtMap = new HashMap<String, Object>();
		// 增值服务明细信息
		VATVasExpensesDt tvasDt = new VATVasExpensesDt();
		tvasDt.setCdOrder(strCdOrder); 					// 进仓编号
		tvasDt.setServiceId(strServiceId); 				// 增值服务ID
		tvasDt.setVasId(strVasId); 					    // 增值服务费用ID
		tvasDt.setVarQuantity(douVasNum); 			    // 数量
		tvasDt.setVarWeight(douVasWeight); 			    // 数量
		tvasDt.setVasExpensesDtId(strVasExpensesDtId);	// 增值服务费用明细ID
		tvasDt.setVasBelong(intVasBelong);				// 耗材所属
		tvasDtMap.put("tvasDt", tvasDt);				// 增值服务
		if(strFlgDefault==null){
			strFlgDefault = "";
		}
		tvasDtMap.put("vasType", strFlgDefault);        // 缺省科目
		NetworkHelper.getInstance().postJsonData(_thisActivity,"VA004_addOrUpdateVasExpenses",tvasDtMap,
			null,new AmassHttpResponseHandler<VasVA004>() {
					@Override
					protected void OnSuccess(VasVA004 response) {
						super.OnSuccess(response);
						refresh();  // 刷新画面
					}
				}, true);
	}
	
	// “刷新”画面
	public void refresh(){
		Intent intent = new Intent(this, VA004Activity.class);
		intent.putExtra("strCdOrder", strCdOrder);
		intent.putExtra("strServiceId", strServiceId);
		intent.putExtra("strFlgDefault", strFlgDefault);
		intent.putExtra("materialNum", materialNum);
		intent.putExtra("materialCbm", materialCbm);
		intent.putExtra("materialWeight", materialWeight);
		startActivity(intent);
		_thisActivity.finish();
	}
	
	// 处理耗材信息
	// flg:"delete" 删除    "add" 添加
	public void DoWithVasExpenses(final VasVA004 vs004,String flg){
		if("delete".equals(flg)){
			Map<String, Object> tvasDtMap = new HashMap<String, Object>();
			tvasDtMap.put("vasExpensesDtId", vs004.getVasExpensesDtId());   // 增值服务费用ID

			NetworkHelper.getInstance().postJsonData(_thisActivity,"VA004_DeleteVasExpenses",
				tvasDtMap,VasVA004.class,
				new AmassHttpResponseHandler<VasVA004>() {
					@Override
					protected void OnSuccess(VasVA004 response) {
						super.OnSuccess(response);
						VasVA004 oldVs004 = (VasVA004) response;
						// 判断是否已经提交审核
						if (0 != oldVs004.getFlgAppoval()) {
							vs004.setIsSelect(true);
							vasAdapter.notifyDataSetChanged();
							Utils.showAlertDialog(_thisActivity,getString(R.string.VA004_DELETE_DEBUG));
						} else {
							refresh();  // 刷新画面
						}
					}
				}, true);
/*		}else if("add".equals(flg)){
			Map<String, Object> tvasDtMap = new HashMap<String, Object>();
			// 增值服务明细信息
			VATVasExpensesDt tvasDt = new VATVasExpensesDt();
			tvasDt.setCdOrder(strCdOrder); 				// 进仓编号
			tvasDt.setServiceId(strServiceId);      	// 增值服务ID
			tvasDt.setVasId(vs004.getVasId());         	// 增值服务费用ID
			tvasDt.setVasChaName(vs004.getVasNmChn()); 	// 增值服务名称
			tvasDt.setVarPrice(vs004.getVasPrice()); 	// 单价
			tvasDt.setVarQuantity((double) 1); 			// 数量
			tvasDt.setVarAmount(vs004.getVasPrice()); 	// 总价
			tvasDt.setVarUit(vs004.getVasUnit()); 		// 单位
			tvasDtMap.put("tvasDt", tvasDt);			// 增值服务

			NetworkHelper.getInstance().postJsonData(_thisActivity,"VA004_addOrUpdateVasExpenses",
					tvasDtMap,VasVA004.class,
					new AmassHttpResponseHandler<VasVA004>() {
						@Override
						protected void OnSuccess(VasVA004 response) {
							super.OnSuccess(response);
							refresh();  // 刷新画面
						}
					}, true);*/
		}
	}
	// add by yxq 2014/05/22 end
	// add by yxq 2014/09/23 begin
	public String  GetNum(String vasCode){
		String num = "";
		for(int i = 0 ;i < vasSumList.size() ; i++){
			if(vasCode.equals(vasSumList.get(i).getId())
					&& 1 == vasSumList.get(i).getFlgType()){
				/*if(!"0".equals(vasSumList.get(i).getName())){
					num = vasSumList.get(i).getName();
				}*/
				DecimalFormat df = new DecimalFormat("#0.0000");   
				num = String.valueOf(df.format(materialCbm));
				break;
			}
		}
		if(num == null || "".equals(num)){
		    num = String.valueOf(materialNum);
		}
		return num;
	}
	
	public String GetWeight(String vasCode){
		String weight = "0.0";
		
		for(int i = 0 ;i < vasAdapter.getCount() ; i++){
			VasVA004 data = (VasVA004) vasAdapter.getItem(i);
			if (vasCode.equals(data.getVasId())){
				if (data.getWeight() != null){
					return data.getWeight().toString();
				}
				else{
					return "0.0";
				}
			}
		}

		return weight;
	}

	// add by yxq 2014/09/23 end
	
	// add by yxq 2014/09/26 begin
	// 根据选择的耗材显示
	public void DescBySpVas(){
		strVasId = ((SelectDict) spVas.getSelectedItem()).getId();
		strVasName = ((SelectDict) spVas.getSelectedItem()).getName();
		if("ID_NONE".equals(strVasId)) {    // 无耗材
			linLay.setVisibility(View.GONE);
		}
		else{
			linLay.setVisibility(View.VISIBLE);
		}
	}
	// add by yxq 2014/09/26 end
}
