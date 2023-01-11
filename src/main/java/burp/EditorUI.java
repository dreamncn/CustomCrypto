/*
 * Created by JFormDesigner on Mon Jan 09 15:34:21 CST 2023
 */

package burp;

import burp.core.Rule;
import burp.core.Rules;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListDataListener;


/**
 * @author unknown
 */
public class EditorUI  {

    public JSplitPane getSplitPane1() {
        return splitPane1;
    }

    public EditorUI(Component component) {
        splitPane1 = new JSplitPane();
        JPanel panel1 = new JPanel();
        JLabel label1 = new JLabel();
        comboBox1 = new JComboBox();
        JLabel label2 = new JLabel();
        JPanel panel2 = new JPanel();

        //======== splitPane1 ========

        splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);

        //======== panel1 ========

        panel1.setMinimumSize(new Dimension(149, 60));
        panel1.setPreferredSize(new Dimension(75, 30));
        panel1.setMaximumSize(new Dimension(2147483647, 60));
        panel1.setLayout(new BorderLayout());
        //---- label1 ----
        label1.setText("\u8bf7\u9009\u62e9\u811a\u672c  ");
        label1.setPreferredSize(new Dimension(100, 30));
        panel1.add(label1, BorderLayout.WEST);

        //---- comboBox1 ----
        comboBox1.setPreferredSize(new Dimension(0, 15));
        comboBox1.addActionListener(e -> selectItem(e));
        panel1.add(comboBox1, BorderLayout.CENTER);

        //---- label2 ----
        label2.setText("\u53d1\u9001\u6570\u636e\u5305\u8bf7\u4e0d\u8981\u4f7f\u7528\u89e3\u5bc6\u540e\u7684\u7ed3\u679c\u3002");
        panel1.add(label2, BorderLayout.NORTH);

        splitPane1.setTopComponent(panel1);

        //======== panel2 ========
        panel2.setLayout(new BorderLayout());
        splitPane1.setBottomComponent(panel2);

        BurpExtender.print("组件："+component.toString());

        panel2.add(component,BorderLayout.CENTER);

        class Model implements ComboBoxModel<Rule>{
            private final ArrayList<Rule> arrayList;
            private Rule rule = null;
            Model(){
                arrayList = (new Rules()).getAll();
            }
            @Override
            public void setSelectedItem(Object anItem) {
                rule = (Rule) anItem;
            }
            @Override
            public Object getSelectedItem() {
                return rule;
            }
            @Override
            public int getSize() {
                return arrayList.size();
            }

            @Override
            public Rule getElementAt(int index) {
                return arrayList.get(index);
            }

            @Override
            public void addListDataListener(ListDataListener l) {

            }

            @Override
            public void removeListDataListener(ListDataListener l) {

            }
        }
        comboBox1.setModel(new Model());
    }

    private void selectItem(ActionEvent e) {
        Rule rule = (Rule)comboBox1.getSelectedItem();
        assert rule != null;
        BurpExtender.print("规则："+rule.toString());
    }
    private final JSplitPane splitPane1;
    private final JComboBox comboBox1;
}
