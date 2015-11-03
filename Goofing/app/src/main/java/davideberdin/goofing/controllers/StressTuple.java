package davideberdin.goofing.controllers;

public class StressTuple<T, V>
{
    private T phoneme;
    private V isStress;

    public StressTuple(T phoneme, V isStress){
        this.phoneme = phoneme;
        this.isStress = isStress;
    }

    public T getPhoneme() { return phoneme; }
    public V getIsStress() { return isStress; }
}
