function selectItem(elementId, selectedOption)
{
	options = document.getElementById(elementId).options
	for (var i = 0; i < options.length; i++)
	{
		if (options[i].value == selectedOption)
			options[i].selected = true;
	}
}
