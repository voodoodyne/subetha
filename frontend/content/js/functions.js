function selectItem(elementId, selectedOption)
{
	options = document.getElementById(elementId).options
	for (var i = 0; i < options.length; i++)
	{
		if (options[i].value == selectedOption)
			options[i].selected = true;
	}
}

function enableChanged(button, field, origValue)
{
	if (origValue != document.getElementById(field).value)
	{
		document.getElementById(button).disabled=false;
	}
	else
	{ 
		document.getElementById(button).disabled=true;
	}
	return true;
}
