document.addEventListener("DOMContentLoaded", () => {
  const command = document.querySelector(".command__code");
  const copyButton = document.querySelector(".command__copy");
  const copyText = document.querySelector(".copy-text");
  const copiedText = document.querySelector(".copied-text");

  let copyButtonTimeoutId;

  copyButton.addEventListener("click", async () => {
    try {
      await navigator.clipboard.writeText(command.textContent.trim());

      copyText.style.visibility = "hidden";
      copiedText.style.visibility = "visible";
      copyButton.style.background = "#00A699";

      if (copyButtonTimeoutId) {
        clearTimeout(copyButtonTimeoutId);
      }

      copyButtonTimeoutId = setTimeout(() => {
        copyText.style.visibility = "visible";
        copiedText.style.visibility = "hidden";
        copyButton.style.background = "#FF5A60";
      }, 2000);
    } catch (err) {
      console.error("Failed to copy text: ", err);

      fallbackCopy();
    }
  });

  // Fallback for browsers that don't support clipboard API
  function fallbackCopy() {
    const textArea = document.createElement("textarea");
    textArea.value = command.textContent.trim();
    textArea.style.position = "fixed";
    textArea.style.left = "-999999px";
    textArea.style.top = "-999999px";
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try {
      document.execCommand("copy");
      copyText.textContent = "Copied!";
      copyButton.style.background = "#00A699";

      setTimeout(() => {
        copyText.textContent = "Copy";
        copyButton.style.background = "#FF5A60";
      }, 2000);
    } catch (err) {
      console.error("Fallback copy failed: ", err);
    }

    document.body.removeChild(textArea);
  }

  document.addEventListener("keydown", (e) => {
    if (e.ctrlKey && e.key === "c") {
      e.preventDefault();
      copyButton.click();
    }
  });
});
