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

function checkSame(elementA, elementB, submitButton)
{
	if (document.getElementById(elementA).value == document.getElementById(elementB).value)
	{
		document.getElementById(submitButton).disabled=false;
	}
	else
	{
		document.getElementById(submitButton).disabled=true;
	}
	return true;				
}

function enableSingleField(field, button)
{
	if (document.getElementById(field).value != "")
	{
		document.getElementById(button).disabled=false;
	}
	else
	{
		document.getElementById(button).disabled=true;
	}
	return true;
}
