package com.raven.datechooser.demo;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.raven.datechooser.DateBetween;
import com.raven.datechooser.DateChooser;
import com.raven.datechooser.listener.DateChooserAction;
import com.raven.datechooser.listener.DateChooserAdapter;
import net.miginfocom.swing.MigLayout;

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
        JPanel panel = new JPanel(new MigLayout("fill, inset 50", "", "top"));
        getContentPane().add(panel);
        DateChooser ch = new DateChooser();
        ch.addActionDateChooserListener(
                new DateChooserAdapter() {
                    @Override
                    public void dateChanged(Date date, DateChooserAction action) {
                        System.out.println("date single selected...");
                    }

                    @Override
                    public void dateBetweenChanged(DateBetween date, DateChooserAction action) {
                        System.out.println("date between selected...");
                    }
                });
        JTextField txt = new JTextField();
        ch.setTextField(txt);
        panel.add(txt, "w 300, wrap");
        JButton cmd = new JButton("Selected Date");
        ch.setDateSelectionMode(DateChooser.DateSelectionMode.BETWEEN_DATE_SELECTED);
        ch.toDay();
        cmd.addActionListener(
                e -> {
                    // SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    // DateBetween between = ch.getSelectedDateBetween();
                    //  System.out.println(df.format(between.getFromDate()) + " to " + df.format(between.getToDate()));

                    //  System.out.println(df.format(ch.getSelectedDate()));
                    ch.setSelectedDateBetween(3, 3, 2022, 5, 7, 2022, true);
                    ch.setLabelCurrentDayVisible(false);
                });
        panel.add(cmd);
    }

    public static void main(String[] args) {
       FlatDarculaLaf.registerCustomDefaultsSource("com.raven.datechooser.demo");
        FlatDarculaLaf.setup();

        java.awt.EventQueue.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        new Demo().setVisible(true);
                    }
                });
    }
}
