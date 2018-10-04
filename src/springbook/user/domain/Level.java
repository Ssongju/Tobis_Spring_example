package springbook.user.domain;

public enum Level {
	// �� ���� �̴� ������Ʈ ����
	GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER);
	
	private final int value;
	private final Level next;
	
	//DB�� ������ ���� �־��ִ� ������.	
	Level(int value, Level next) {
		this.value = value;
		this.next = next;
	}
	
	//���� �������� �޼ҵ�
	public int intValue() {
		return value;
	}
	
	public Level nextLevel() {
		return this.next;
	}
	
	//�����κ��� LevelŸ�� ������Ʈ�� ���������� ���� ����ƽ �޼ҵ�
	public static Level valueOf(int value) {
		switch(value) {
			case 1: return BASIC;
			//case 1: return BRONZE;
			case 2: return SILVER;
			case 3: return GOLD;
			default: throw new AssertionError("Unknown value: " + value);
		}
	}
}

