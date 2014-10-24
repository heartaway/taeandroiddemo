package com.taobao.tae.buyingdemo.widget.buttonmenu;

import com.taobao.tae.buyingdemo.R;
import com.taobao.tae.buyingdemo.widget.buttonmenu.viewmodel.button.ButtonWithMutableSubjectAndResourceVM;
import com.taobao.tae.buyingdemo.widget.buttonmenu.viewmodel.buttonmenu.SimpleButtonMenuVM;

/**
 * <p></p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/8/13
 * Time: 上午11:58
 */
public class CustomButtonMenu extends SimpleButtonMenuVM {

    public final ButtonWithMutableSubjectAndResourceVM index = new ButtonWithMutableSubjectAndResourceVM(R.layout.menu_index_button, true, R.id.menu_index_btn,
            new int[]{R.id.menu_index_btn}, null, R.id.menu_index_btn,
            R.id.menu_index_btn);
    public final ButtonWithMutableSubjectAndResourceVM category = new ButtonWithMutableSubjectAndResourceVM(R.layout.menu_category_button, true, R.id.menu_category_btn,
            new int[]{R.id.menu_category_btn}, null, R.id.menu_category_btn,
            R.id.menu_category_btn);
    public final ButtonWithMutableSubjectAndResourceVM my = new ButtonWithMutableSubjectAndResourceVM(R.layout.menu_my_button, true, R.id.menu_my_btn,
            new int[]{R.id.menu_my_btn}, null, R.id.menu_my_btn,
            R.id.menu_my_btn);


    public CustomButtonMenu() {
        super();
        addItem(index);
        addItem(category);
        addItem(my);
    }




}
