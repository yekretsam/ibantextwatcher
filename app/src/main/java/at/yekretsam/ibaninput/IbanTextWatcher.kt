package at.yekretsam.ibaninput

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class IbanTextWatcher(private val editText: EditText) : TextWatcher {
    private var isSpaceDel = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // check if the user is about to delete a single space
        isSpaceDel = count == 1 && after == 0 && s!![start] == ' '
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        editText.removeTextChangedListener(this)

        var result = ""
        var resultSel = editText.selectionEnd
        // rebuild the entire text without whitespaces
        // keeping count of the cursor pos
        for(seqI in s!!.indices) {
            if(s[seqI] != ' ') {
                result += s[seqI]
            } else if(seqI < editText.selectionEnd) {
                resultSel -= 1
            }
        }

        // Special case in which the cursor is right after a space,
        // we want to additionally delete the digit before the space
        if(isSpaceDel) {
            result = result.removeRange(resultSel-1, resultSel)
            resultSel -= 1
        }

        // Format the text again
        var i = 1
        var spaces = 0
        while(i < result.length) {
            /*
            i == 2 -> space after the first two digits
            (i > 4 && (i-2 - spaces) % 4 == 0) -> the rest, basically adding spaces every 4 digits getting the first two digits and the already add spaces out of the picture
             */
            if(i == 2 || (i > 4 && (i - 2 - spaces) % 4 == 0)) {
                result  = result.substring(0, i) + " " + result.substring(i)
                i++
                spaces++
                // only update the cursor if spaces are added before it
                if(i <= resultSel) {
                    resultSel++
                }
            }
            i++
        }

        // Set text and cursor for the EditText
        editText.setText(result)
        editText.setSelection(resultSel)

        editText.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) { }
}