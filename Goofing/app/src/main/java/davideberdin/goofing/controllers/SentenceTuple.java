package davideberdin.goofing.controllers;

public class SentenceTuple<T, U, V>
{
    private T phonetics;
    private U phonemes;
    private V stress;

    public SentenceTuple(T phonetics, U phonemes, V stress)
    {
        this.phonetics = phonetics;
        this.phonemes = phonemes;
        this.stress = stress;
    }

    public T getPhonetics() { return phonetics; }
    public U getPhonemes() { return phonemes; }
    public V getStress() { return stress; }
}
