package org.apache.fraud.search;

import org.apache.fraud.search.common.UserInterface;

import java.awt.*;

public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(UserInterface::new);
	}
}
