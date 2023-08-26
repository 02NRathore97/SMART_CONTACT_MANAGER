package com.smartcontactmanager.helper;

public class Message {
	
	
		//Attributes
		private String content;
		private String type;
		
		
		//default constructor
		public Message() {
		
		}
		
		//parameterized constructor
		public Message(String content, String type) {
			super();
			this.content = content;
			this.type = type;
		}
		
		//getters setters
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
		//toString method
		@Override
		public String toString() {
			return "Message [content=" + content + ", type=" + type + "]";
		}
		
		
}
