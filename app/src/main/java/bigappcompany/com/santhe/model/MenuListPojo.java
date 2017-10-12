package bigappcompany.com.santhe.model;

public class MenuListPojo {
	
	private String item;
	
	public String getCity_id() {
		return city_id;
	}
	
	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}
	
	private String city_id;
	private int selected;
	private int notSelected;
	private Boolean isSelected, menuClick;
	
	
	private int selectedImg;
	private int deselectedImg;
	
	public int getSelectedImg() {
		return selectedImg;
	}
	
	public void setSelectedImg(int selectedImg) {
		this.selectedImg = selectedImg;
	}
	
	public int getDeselectedImg() {
		return deselectedImg;
	}
	
	public void setDeselectedImg(int deselectedImg) {
		this.deselectedImg = deselectedImg;
	}
	
	public String getItem() {
		return item;
	}
	
	public void setItem(String item) {
		this.item = item;
	}
	
	
	public int getSelected() {
		return selected;
	}
	
	public void setSelected(int selected) {
		this.selected = selected;
	}
	
	public int getNotSelected() {
		return notSelected;
	}
	
	public void setNotSelected(int notSelected) {
		this.notSelected = notSelected;
	}
	
	public Boolean getIsSelected() {
		return isSelected;
	}
	
	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public Boolean getMenuClick() {
		return menuClick;
	}
	
	public void setMenuClick(Boolean menuClick) {
		this.menuClick = menuClick;
	}
	
	
}
