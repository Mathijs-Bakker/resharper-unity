using UnityEditor;

public static class A 
{
	[SettingsProvider]
    public static SettingsProvider CreateMyCustomSettingsProvider()
    {
        return null;
    }
}