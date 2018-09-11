package com.longforus.mvpautocodeplus.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.MutableCollectionComboBoxModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.longforus.mvpautocodeplus.ConsKt;
import com.longforus.mvpautocodeplus.Utils;
import com.longforus.mvpautocodeplus.config.ItemConfigBean;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import org.apache.http.util.TextUtils;

public class EnterKeywordDialog extends JDialog {
    private static final String NAME_CHECK_STR = "[a-zA-Z]+[0-9a-zA-Z_]";
    private final Project mProject;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField et_name;
    private JRadioButton mJavaRadioButton;
    private JRadioButton mKotlinRadioButton;
    private JRadioButton mActivityRadioButton;
    private JRadioButton mFragmentRadioButton;
    private JCheckBox mViewCheckBox;
    private JCheckBox mPresenterCheckBox;
    private JCheckBox mModelCheckBox;
    private JComboBox<String> cob_v;
    private JComboBox<String> cob_p;
    private JComboBox<String> cob_m;
    private JRadioButton mGlobalRadioButton;
    private JRadioButton mCurrentProjectRadioButton;
    private JCheckBox mcbModel;
    private OnOkListener onOkListener;
    private PropertiesComponent mState;

    private EnterKeywordDialog(Project project) {
        mProject = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        mGlobalRadioButton.addItemListener(e -> {
            if (e.getID() != ItemEvent.ITEM_STATE_CHANGED) {
                return;
            }
            if (mGlobalRadioButton.isSelected()) {
                mState = PropertiesComponent.getInstance();
                PropertiesComponent.getInstance(mProject).setValue(ConsKt.USE_PROJECT_CONFIG, false);
            } else {
                mState = PropertiesComponent.getInstance(mProject);
                mState.setValue(ConsKt.USE_PROJECT_CONFIG, true);
            }
            setSavedSuperClass(this, mState);
        });
    }

    public static EnterKeywordDialog getDialog(Project project, OnOkListener onOkListener) {
        EnterKeywordDialog dialog = new EnterKeywordDialog(project);
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - dialog.getWidth() / 2;
        int y = (int) screensize.getHeight() / 2 - dialog.getHeight() / 2;
        dialog.setTitle("MvpAutoCodePlus");
        dialog.setLocation(x, y);
        dialog.onOkListener = onOkListener;
        PropertiesComponent state = PropertiesComponent.getInstance(project);
        boolean useProjectConfig = state.getBoolean(ConsKt.USE_PROJECT_CONFIG, false);
        if (useProjectConfig) {
            dialog.mState = state;
            dialog.mCurrentProjectRadioButton.setSelected(true);
        } else {
            dialog.mState = PropertiesComponent.getInstance();
            setSavedSuperClass(dialog, dialog.mState);
        }
        dialog.pack();
        dialog.setVisible(true);
        return dialog;
    }

    private static void setSavedSuperClass(EnterKeywordDialog dialog, PropertiesComponent state) {
        dialog.mActivityRadioButton.addChangeListener(e -> {
            if (dialog.mActivityRadioButton.isSelected()) {
                setSuperClass(dialog.cob_v, state.getValue(ConsKt.SUPER_VIEW_ACTIVITY), dialog.mViewCheckBox, ConsKt.IS_NOT_SET + "," + ConsKt.GOTO_SETTING);
            } else {
                setSuperClass(dialog.cob_v, state.getValue(ConsKt.SUPER_VIEW_FRAGMENT), dialog.mViewCheckBox, ConsKt.IS_NOT_SET + "," + ConsKt.GOTO_SETTING);
            }
        });
        setSuperClass(dialog.cob_v, state.getValue(ConsKt.SUPER_VIEW_ACTIVITY), dialog.mViewCheckBox, ConsKt.IS_NOT_SET + "," + ConsKt.GOTO_SETTING);
        setSuperClass(dialog.cob_p, state.getValue(ConsKt.SUPER_PRESENTER_IMPL), null, ConsKt.IS_NOT_SET + "," + ConsKt.NO_SUPER_CLASS);
        setSuperClass(dialog.cob_m, state.getValue(ConsKt.SUPER_MODEL_IMPL), null, ConsKt.IS_NOT_SET + "," + ConsKt.NO_SUPER_CLASS);
    }

    private static void setSuperClass(JComboBox<String> cob, String value, JCheckBox jcb, String nullShowStr) {
        if (TextUtils.isEmpty(value)) {
            value = nullShowStr;
            if (jcb != null) {
                jcb.setSelected(false);
            }
        } else if (jcb != null) {
            jcb.setSelected(true);
        }
        cob.setModel(new MutableCollectionComboBoxModel<String>(Arrays.asList(value.split(";"))));
        cob.setSelectedIndex(0);
    }

    private static String getSelectedContent(JComboBox<String> cob) {
        Object item = cob.getSelectedItem();
        if (item == null) {
            return "";
        } else {
            return (String) item;
        }
    }

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private boolean checkImplementInValid(JCheckBox cb, JComboBox<String> cob, String item) {
        if (cb.isSelected()) {
            String value = (String) cob.getSelectedItem();
            if (TextUtils.isEmpty(value) || ConsKt.IS_NOT_SET.equals(value)) {
                Messages.showErrorDialog(item + " implement is invalid ", "Error");
                return true;
            }
        }
        return false;
    }

    private void onOK() {
        String sv = mState.getValue(ConsKt.SUPER_VIEW);
        String sp = mState.getValue(ConsKt.SUPER_PRESENTER);
        String sm = mState.getValue(ConsKt.SUPER_MODEL);
        if (TextUtils.isEmpty(sm) || TextUtils.isEmpty(sp) || TextUtils.isEmpty(sv)) {
            Messages.showErrorDialog("Has Super interface not set," + ConsKt.GOTO_SETTING, "Error");
            return;
        }
        String key = et_name.getText();
        if (Utils.isEmpty(key)) {
            Messages.showErrorDialog("Name not allow empty!", "Error");
            return;
        }
        if (!key.matches(NAME_CHECK_STR)) {
            Messages.showErrorDialog("An illegal name!", "Error");
            return;
        }
        if (checkImplementInValid(mViewCheckBox, cob_v, "View")) {
            return;
        }
        if (checkImplementInValid(mPresenterCheckBox, cob_p, "Presenter")) {
            return;
        }
        if (checkImplementInValid(mModelCheckBox, cob_m, "Model")) {
            return;
        }
        buttonOK.setEnabled(false);
        if (onOkListener != null) {
            onOkListener.onOk(new ItemConfigBean(key, mJavaRadioButton.isSelected(), mActivityRadioButton.isSelected(), getSelectedContent(cob_v), getSelectedContent(cob_p),
                getSelectedContent(cob_m), mState, mcbModel.isSelected()));
        }
        dispose();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(6, 2, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setMinimumSize(new Dimension(400, 376));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1,
            new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2,
            new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3,
            new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Enter Name:");
        panel3.add(label1,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null,
                null, 0, false));
        et_name = new JTextField();
        et_name.setText("");
        panel3.add(et_name,
            new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                null, new Dimension(150, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel4,
            new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder("Implement code type:"));
        mJavaRadioButton = new JRadioButton();
        mJavaRadioButton.setSelected(true);
        mJavaRadioButton.setText("Java");
        panel4.add(mJavaRadioButton,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mKotlinRadioButton = new JRadioButton();
        mKotlinRadioButton.setText("Kotlin");
        panel4.add(mKotlinRadioButton,
            new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        contentPane.add(panel5,
            new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder("Generate implement item:"));
        mViewCheckBox = new JCheckBox();
        mViewCheckBox.setSelected(true);
        mViewCheckBox.setText("View");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(mViewCheckBox, gbc);
        mPresenterCheckBox = new JCheckBox();
        mPresenterCheckBox.setSelected(true);
        mPresenterCheckBox.setText("Presenter");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(mPresenterCheckBox, gbc);
        mModelCheckBox = new JCheckBox();
        mModelCheckBox.setSelected(true);
        mModelCheckBox.setText("Model");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(mModelCheckBox, gbc);
        cob_m = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 20.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(cob_m, gbc);
        cob_p = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(cob_p, gbc);
        cob_v = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(cob_v, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Settings Mode:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(label2, gbc);
        mGlobalRadioButton = new JRadioButton();
        mGlobalRadioButton.setSelected(true);
        mGlobalRadioButton.setText("Global");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(mGlobalRadioButton, gbc);
        mCurrentProjectRadioButton = new JRadioButton();
        mCurrentProjectRadioButton.setText("Current Project");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(mCurrentProjectRadioButton, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 100;
        panel5.add(spacer2, gbc);
        final Spacer spacer3 = new Spacer();
        contentPane.add(spacer3,
            new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 50), null,
                null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel6.setToolTipText("View implement type:");
        contentPane.add(panel6,
            new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder("View implement type:"));
        mActivityRadioButton = new JRadioButton();
        mActivityRadioButton.setSelected(true);
        mActivityRadioButton.setText("Acitivy");
        panel6.add(mActivityRadioButton,
            new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mFragmentRadioButton = new JRadioButton();
        mFragmentRadioButton.setText("Fragment");
        panel6.add(mFragmentRadioButton,
            new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mcbModel = new JCheckBox();
        mcbModel.setSelected(true);
        mcbModel.setText("Generate IModel");
        contentPane.add(mcbModel,
            new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(mJavaRadioButton);
        buttonGroup.add(mKotlinRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(mActivityRadioButton);
        buttonGroup.add(mFragmentRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(mGlobalRadioButton);
        buttonGroup.add(mCurrentProjectRadioButton);
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    @FunctionalInterface
    public interface OnOkListener {
        void onOk(ItemConfigBean str);
    }
}
