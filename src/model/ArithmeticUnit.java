package model;

public class ArithmeticUnit {
    private int value;
    private ArithmeticUnit left;
    private ArithmeticUnit right;

    public int getValue() {
        return value;
    }

    public ArithmeticUnit(int value) {
        this.value = value;
    }
    public ArithmeticUnit(String value) {
        try{
            this.value = Integer.parseInt(value);
        }catch (NumberFormatException e){
            int index=calculateLeftIndex(value);
            calculateRight(value.substring(index));
        }
    }
    private void calculateValue(String operator) {
        value=ArithmeticOperation.getOperation(operator).apply(left,right);
    }

    private void calculateRight(String value) {
        String operator=value.charAt(0)+"";
        value=value.substring(1);

    }

    private int calculateLeftIndex(String value){
        boolean priorityActive=false;
        int index=0;
        String operation = "";
        for(int i = 0; i < value.length(); i++){
            if(!Character.isDigit(value.charAt(i))){
                if(priorityActive){
                    left=new ArithmeticUnit(ArithmeticOperation.getOperation(operation).apply(left,new ArithmeticUnit(Integer.parseInt(value.substring(index,i)))));
                }
                if (index==0) left=new ArithmeticUnit(Integer.parseInt(value.substring(0,i)));
                if (ArithmeticOperation.hasPriority(value.charAt(i)+"")){
                    index=i+1;
                    operation=value.charAt(i)+"";
                    priorityActive=true;
                }else {
                    return i;
                }
            }
        }
        return value.length();
    }

    private ArithmeticUnit calculatePriority(String value){
        boolean priorityActive=false;
        int index=0;
        String operation = "";
        ArithmeticUnit unit=new ArithmeticUnit(0);
        for(int i = 0; i < value.length(); i++){
            if(!Character.isDigit(value.charAt(i))){
                if(priorityActive){
                    unit=new ArithmeticUnit(ArithmeticOperation.getOperation(operation).apply(unit,new ArithmeticUnit(Integer.parseInt(value.substring(index,i)))));
                }
                if (index==0) unit=new ArithmeticUnit(Integer.parseInt(value.substring(0,i)));
                if (ArithmeticOperation.hasPriority(value.charAt(i)+"")){
                    index=i+1;
                    operation=value.charAt(i)+"";
                    priorityActive=true;
                }else {
                    return unit;
                }
            }
        }
        return unit;
    }
    @Override
    public String toString() {
        return "ArithmeticUnit{" +
                "left=" + left +
                ", value=" + value +
                ", right=" + right +
                '}';
    }
}
