/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guis;

import data.BotConfig;
import irc.*;

/**
 *
 * @author young234
 */
public class FiltersGUI extends javax.swing.JFrame {
    
    private BotConfig c;
    
    public FiltersGUI(String title, BotConfig c) {
        super(title);
        this.c = c;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CAPS_LOCK_FILTER = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        LINKS_FILTER = new javax.swing.JCheckBox();
        BANNED_WORDS_FILTER = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Options");
        setResizable(false);

        CAPS_LOCK_FILTER.setText("Caps Lock");
        CAPS_LOCK_FILTER.setSelected(c.getCapsLockFilter());
        CAPS_LOCK_FILTER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CAPS_LOCK_FILTERActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Filters");

        LINKS_FILTER.setText("Links");
        LINKS_FILTER.setSelected(c.getLinksFitler());
        LINKS_FILTER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LINKS_FILTERActionPerformed(evt);
            }
        });

        BANNED_WORDS_FILTER.setText("Ban Words");
        BANNED_WORDS_FILTER.setSelected(c.getLinksFitler());
        BANNED_WORDS_FILTER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BANNED_WORDS_FILTERActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CAPS_LOCK_FILTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LINKS_FILTER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BANNED_WORDS_FILTER)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 25, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CAPS_LOCK_FILTER)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LINKS_FILTER)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BANNED_WORDS_FILTER)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CAPS_LOCK_FILTERActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CAPS_LOCK_FILTERActionPerformed
        c.setCapsLockFilter(CAPS_LOCK_FILTER.isSelected());
        if (IRC.isConnected()) {
            IRC.setCapsLockFilterStatus(CAPS_LOCK_FILTER.isSelected());
        }
    }//GEN-LAST:event_CAPS_LOCK_FILTERActionPerformed

    private void LINKS_FILTERActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LINKS_FILTERActionPerformed
        c.setLinksFilter(LINKS_FILTER.isSelected());
        if (IRC.isConnected()) {
            IRC.setLinksFilterStatus(LINKS_FILTER.isSelected());
        }
    }//GEN-LAST:event_LINKS_FILTERActionPerformed

    private void BANNED_WORDS_FILTERActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BANNED_WORDS_FILTERActionPerformed
       c.setLinksFilter(LINKS_FILTER.isSelected());
        if (IRC.isConnected()) {
            IRC.setLinksFilterStatus(LINKS_FILTER.isSelected());
        }
    }//GEN-LAST:event_BANNED_WORDS_FILTERActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox BANNED_WORDS_FILTER;
    private javax.swing.JCheckBox CAPS_LOCK_FILTER;
    private javax.swing.JCheckBox LINKS_FILTER;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
