package com.jjforever.wgj.maincalendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jjforever.wgj.maincalendar.BLL.DailyRecordMng;
import com.jjforever.wgj.maincalendar.Model.DailyRecord;
import com.jjforever.wgj.maincalendar.Model.ICalendarRecord;
import com.jjforever.wgj.maincalendar.dialogpicker.picker.DialogPicker;
import com.jjforever.wgj.maincalendar.toolbar.ToolBarActivity;

import java.util.ArrayList;
import java.util.Collections;

public class DailyRecordList extends ToolBarActivity {

    // 时间轴列表
    private ListView mListView;
    // 记录适配器
    private RecordAdapter mRecordAdapter;
    // 当前编辑的记录位置
    private int mEditPosition = -1;
    // 是否进入多选模式
    private boolean mMultiSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        setTitle(getResources().getString(R.string.action_daily));
        showOkBtn(false);
        setOkBtnImage(R.drawable.icon_delete);
        setView();
    }

    /**
     * 设置界面控件
     */
    private void setView() {
        mListView = (ListView) findViewById(R.id.record_list);
        if (mListView != null) {
            // 点击事件
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mRecordAdapter == null || mMultiSelected) {
                        return;
                    }

                    Object selItem = mRecordAdapter.getItem(position);
                    if (selItem == null) {
                        return;
                    }

                    mEditPosition = position;
                    startEditDaily((DailyRecord) selItem);
                }
            });
            // 长按进入多选模式
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    mMultiSelected = true;
                    mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    showOkBtn(true);
                    return false;
                }
            });
        }

        loadAllRecord();
    }

    /**
     * 载入所有记录
     */
    private void loadAllRecord() {
        ArrayList<DailyRecord> selectLst = DailyRecordMng.selectAll();
        ArrayList<ICalendarRecord> tmpLst = new ArrayList<>();
        tmpLst.addAll(selectLst);
        RecordDateComparator comparator = new RecordDateComparator();
        Collections.sort(tmpLst, comparator);
        // ListView绑定适配器
        mRecordAdapter = new RecordAdapter(this, true, tmpLst);
        mListView.setAdapter(mRecordAdapter);
    }

    /**
     * 编辑日程
     *
     * @param record 要编辑的记录
     */
    private void startEditDaily(DailyRecord record) {
        Intent intent = new Intent(this, AddDailyActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(AppConstants.MAIN_ACTIVITY_CLICK_DATE, record);
        intent.putExtras(mBundle);
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mEditPosition < 0) {
            return;
        }
        if (resultCode == RESULT_OK) {
            // 编辑
            Object selItem = mRecordAdapter.getItem(mEditPosition);
            if (selItem == null) {
                return;
            }
            mRecordAdapter.setItem(mEditPosition, DailyRecordMng.select(((DailyRecord) selItem).getIndex()));
            mListView.setAdapter(mRecordAdapter);
        } else if (resultCode == AppConstants.RESULT_DELETE) {
            // 删除
            mRecordAdapter.removeItem(mEditPosition);
            mListView.setAdapter(mRecordAdapter);
        }
    }

    @Override
    public void onOKButtonClick() {
        if (!mMultiSelected) {
            return;
        }

        if (mListView.getCheckedItemCount() <= 0) {
            Toast.makeText(this,
                    getResources().getString(R.string.please_select_delete_item),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DialogPicker picker = new DialogPicker(this, getResources().getString(R.string.confirm_delete));
        picker.setOnDialogPickListener(new DialogPicker.OnDialogPickListener() {
            @Override
            public void onDialogPicked(boolean confirm) {
                if (confirm) {
                    long[] checkedIds = mListView.getCheckedItemIds();
                    ArrayList<Long> indexList = new ArrayList<>(checkedIds.length);
                    for (long tmpId : checkedIds) {
                        DailyRecord tmpRecord = (DailyRecord) mRecordAdapter.getItem((int) tmpId);
                        if (tmpRecord != null) {
                            indexList.add(tmpRecord.getIndex());
                        }
                    }
                    if (DailyRecordMng.delete(indexList)) {
                        Toast.makeText(DailyRecordList.this,
                                getResources().getString(R.string.delete_success),
                                Toast.LENGTH_SHORT).show();
                        mMultiSelected = false;
                        showOkBtn(false);
                        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                        // 删除条目
                        for (int i = checkedIds.length - 1; i >= 0; i--) {
                            mRecordAdapter.removeItem((int) checkedIds[i]);
                        }
                        mListView.setAdapter(mRecordAdapter);
                    } else {
                        Toast.makeText(DailyRecordList.this,
                                getResources().getString(R.string.delete_fail),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        picker.show();
    }

    @Override
    public void finish() {
        if (mMultiSelected && mListView.getCheckedItemCount() > 0) {
            // 在多选模式返回键则退出多选
            mMultiSelected = false;
            showOkBtn(false);
            mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            mListView.setAdapter(mRecordAdapter);
            return;
        }
        setResult(RESULT_OK, null);
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
