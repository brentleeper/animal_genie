/*-----------------------------------------------------------------------------------
Name: 			Brent Leeper
Date: 			12/01/17
Course: 			2336.02
Semester: 		Fall 2017
Assignment #: 	
Description: 	Used to move though a GameTree and allows the user to update each node 
				as required. Once changes are made, the GameTree will be updated in memory
				and can be saved to a file for future game play. As this class is not
				a part of the assignment, notes will not be added. Feel free to use the 
				game editor. The password is: nopeeking	
				
				Note: Game Editor is only accessible via Assignment05_GUI
						
--------------------------------------------------------------------------------------*/

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import com.binarytree.gametree.*;

@SuppressWarnings("serial")
public class GameEditor extends JFrame {

	private JPanel contentPane;
	private JTextField currentRootText;
	private JLabel rootLeftText, rootRightText;
	private JCheckBox isQuestionBox;
	private GameTree gameTree = null;
	private TreeNode currRoot = null;
	private static JFrame frame;

	/**
	 * Create the frame
	 */
	public GameEditor() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setPreferredSize(new Dimension(435, 275));
		
		JLabel lblCurrentRootText = new JLabel("Current Node:");
		lblCurrentRootText.setBounds(10, 57, 118, 14);
		contentPane.add(lblCurrentRootText);
		
		JLabel lblRootLeftsText = new JLabel("Left Node(Yes):");
		lblRootLeftsText.setBounds(10, 92, 111, 14);
		contentPane.add(lblRootLeftsText);
		
		JLabel lblRootRightsText = new JLabel("Right Node(No):");
		lblRootRightsText.setBounds(10, 129, 118, 14);
		contentPane.add(lblRootRightsText);
		
		rootLeftText = new JLabel("");
		rootLeftText.setVerticalAlignment(SwingConstants.TOP);
		rootLeftText.setBounds(133, 92, 297, 25);
		contentPane.add(rootLeftText);
		
		rootRightText = new JLabel("");
		rootRightText.setVerticalAlignment(SwingConstants.TOP);
		rootRightText.setBounds(133, 129, 297, 23);
		contentPane.add(rootRightText);
		
		currentRootText = new JTextField();
		currentRootText.setBounds(110, 54, 306, 20);
		contentPane.add(currentRootText);
		currentRootText.setColumns(10);
		
		JButton goLeft = new JButton("Move Left (Yes)");
		goLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TreeNode backUp = currRoot;
				try {
					currRoot = currRoot.getLeft();
					isQuestionBox.setSelected(currRoot.getItem().isQuestion());
					currentRootText.setText(currRoot.getItem().getName());
					try {
						rootLeftText.setText(currRoot.getLeft().getItem().getName());
					}
					catch(Exception l)
					{
						rootLeftText.setText("Null Node");
					}
				
					try{
						rootRightText.setText(currRoot.getRight().getItem().getName());
					}
					catch(Exception l)
					{
						rootRightText.setText("Null Node");
					}
				}catch(Exception r)
				{
					currRoot = backUp;
					JOptionPane.showMessageDialog(frame, "Cannot Move To Null Node");
				}
			}
		});
		goLeft.setBounds(110, 160, 139, 23);
		contentPane.add(goLeft);
		
		JButton goRight = new JButton("Move Right (No)");
		goRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TreeNode backUp = currRoot;
				try {
					currRoot = currRoot.getRight();
					isQuestionBox.setSelected(currRoot.getItem().isQuestion());
					currentRootText.setText(currRoot.getItem().getName());
					try {
						rootLeftText.setText(currRoot.getLeft().getItem().getName());
					}catch(Exception r)
					{
						rootLeftText.setText("Null Node");
					}
					try{
						rootRightText.setText(currRoot.getRight().getItem().getName());
					}catch(Exception r)
					{
						rootRightText.setText("Null Node");
					}
				}catch(Exception r)
				{
					currRoot = backUp;
					JOptionPane.showMessageDialog(frame, "Cannot Move To Null Node");
				}
			}
		});
		goRight.setBounds(280, 160, 144, 23);
		contentPane.add(goRight);
		
		JButton btnChangeCurrentRoots = new JButton("Update Node");
		btnChangeCurrentRoots.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currRoot.setItem(new Item(currentRootText.getText()));
				currRoot.getItem().setQuestion(isQuestionBox.isSelected());			
				JOptionPane.showMessageDialog(null, "Node Updated");
				currentRootText.setText(currRoot.getItem().getName());
			}
		});
		btnChangeCurrentRoots.setBounds(30, 195, 132, 23);
		contentPane.add(btnChangeCurrentRoots);
		
		JLabel lblGameEditor = new JLabel("Animal Genie Editor");
		lblGameEditor.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblGameEditor.setBounds(141, 11, 202, 14);
		contentPane.add(lblGameEditor);
		
		JButton btnSaveExit = new JButton("Save & Exit");
		btnSaveExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currRoot.setItem(new Item(currentRootText.getText()));
				currRoot.getItem().setQuestion(isQuestionBox.isSelected());
				try {
					AnimalGenie_Desktop game = new AnimalGenie_Desktop();
					game.saveGame(true);
					System.exit(0);
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "Could Not Save Game Tree");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Could Not Save Game Tree");
				}
			}
		});
		btnSaveExit.setBounds(239, 233, 104, 23);
		contentPane.add(btnSaveExit);
		
		JButton btnBackToRoot = new JButton("Back To Root");
		btnBackToRoot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadData(gameTree);
			}
		});
		btnBackToRoot.setBounds(313, 195, 111, 23);
		contentPane.add(btnBackToRoot);
		
		isQuestionBox = new JCheckBox("isQuestion");
		isQuestionBox.setBounds(10, 158, 111, 23);
		contentPane.add(isQuestionBox);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currRoot.setItem(new Item(currentRootText.getText()));
				currRoot.getItem().setQuestion(isQuestionBox.isSelected());
				currentRootText.setText(currRoot.getItem().getName());
				try {
					AnimalGenie_Desktop game = new AnimalGenie_Desktop();
					game.saveGame(false);
					JOptionPane.showMessageDialog(null, "Game Tree Saved");
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "Could Not Save Game Tree");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Could Not Save Game Tree");
				}
			}
		});
		btnSave.setBounds(155, 230, 75, 29);
		contentPane.add(btnSave);
		
		JButton btnExit = new JButton("Back To Game");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					frame.dispose();
					AnimalGenie_Desktop game = new AnimalGenie_Desktop();
					game.saveGame(true);
					game.loadGame();
					game.play();
				} catch (IOException e1) {
					System.exit(0);
				}
			}
		});
		btnExit.setBounds(30, 230, 118, 29);
		contentPane.add(btnExit);
		
		JButton btnExit_1 = new JButton("Exit");
		btnExit_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit_1.setBounds(349, 230, 75, 29);
		contentPane.add(btnExit_1);
		
		JButton setToNullBtn = new JButton("Set To Null");
		setToNullBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currRoot.setItem(null);
				loadData(gameTree);
			}
		});
		setToNullBtn.setBounds(181, 192, 113, 29);
		contentPane.add(setToNullBtn);
		setLocationRelativeTo(null);
	}
	
	public void loadData(GameTree tree)
	{
		gameTree = tree;
		currRoot = gameTree.getRoot();
		currentRootText.setText(currRoot.getItem().getName());
		try {
			rootLeftText.setText(currRoot.getLeft().getItem().getName());
		}catch(Exception r)
		{
			rootLeftText.setText("Null Node");
		}
		try{
			rootRightText.setText(currRoot.getRight().getItem().getName());
		}catch(Exception r)
		{
			rootRightText.setText("Null Node");
		}
		isQuestionBox.setSelected(currRoot.getItem().isQuestion());
		frame = this;
		frame.setSize(new Dimension(475, 300));
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
	}
}
