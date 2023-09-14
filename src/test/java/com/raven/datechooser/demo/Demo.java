package com.raven.datechooser.demo;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.raven.datechooser.DateBetween;
import com.raven.datechooser.DateChooser;
import com.raven.datechooser.DateSelectable;
import com.raven.datechooser.listener.DateChooserAction;
import com.raven.datechooser.listener.DateChooserAdapter;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class Demo extends JFrame {

    public Demo() {
        setTitle("Date Chooser Demo");
        setSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
    }

    private void init() {
        getContentPane().setLayout(new BorderLayout());
        JPanel panel = new JPanel(new FlowLayout());
        getContentPane().add(panel);
        DateChooser ch = new DateChooser();
        ch.setDateSelectable(new DateSelectable() {
            @Override
            public boolean isDateSelectable(Date date) {
                return date.before(new Date());
            }
        });
        ch.addActionDateChooserListener(
                new DateChooserAdapter() {
                    @Override
                    public void dateChanged(Date date, DateChooserAction action) {
                        System.out.println("date single selected...");
                    }

                    @Override
                    public void dateBetweenChanged(DateBetween date, DateChooserAction action) {
                        System.out.println(date);
                    }
                });
        JTextField txt = new JTextField();
        ch.setTextField(txt);
        panel.add(txt);
        JButton cmd = new JButton("Selected Date");
        ch.setDateSelectionMode(DateChooser.DateSelectionMode.BETWEEN_DATE_SELECTED);
        // ch.toDay();
        cmd.addActionListener(
                e -> {
                    // SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    // DateBetween between = ch.getSelectedDateBetween();
                    //  System.out.println(df.format(between.getFromDate()) + " to " + df.format(between.getToDate()));

                    //  System.out.println(df.format(ch.getSelectedDate()));
                    ch.setSelectedDateBetween(3, 3, 2022, 5, 7, 2022, true);
                });
        panel.add(cmd);
        JButton cmdMode = new JButton("Change theme");
        cmdMode.addActionListener(e -> {
            if (!FlatLaf.isLafDark()) {
                EventQueue.invokeLater(() -> {
                    FlatAnimatedLafChange.showSnapshot();
                    FlatMacDarkLaf.setup();
                    FlatLaf.updateUI();
                    FlatAnimatedLafChange.hideSnapshotWithAnimation();
                });
            } else {
                EventQueue.invokeLater(() -> {
                    FlatAnimatedLafChange.showSnapshot();
                    FlatMacLightLaf.setup();
                    FlatLaf.updateUI();
                    FlatAnimatedLafChange.hideSnapshotWithAnimation();
                });
            }
        });
        panel.add(cmdMode);
    }

    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("com.raven.datechooser.demo");
       // String listMonth[] = {"មករា", "កុម្ភៈ", "មីនា", "មេសា", "ឧសភា", "មិថុនា", "កក្កដា", "សីហា", "កញ្ញា", "តុលា", "វិច្ឆិកា", "ធ្នូ"};
       // String listDays[] = {"អាទិត្យ","ច័ន្ទ","អង្គារ","ពុធ","ព្រហ","សុក្រ","សៅរ៍"};
       // UIManager.put("DateChooser.listMonth", listMonth);
       // UIManager.put("DateChooser.listDay", listDays);
        FlatMacDarkLaf.setup();
        java.awt.EventQueue.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        new Demo().setVisible(true);
                    }
                });
    }
}
