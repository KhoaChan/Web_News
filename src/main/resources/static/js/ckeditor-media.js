(function () {
  function renderEditorBootError(textarea, message) {
    var parent = textarea.parentElement;
    if (!parent) {
      return;
    }

    var previousAlert = parent.querySelector("[data-editor-error='true']");
    if (previousAlert) {
      previousAlert.remove();
    }

    var alert = document.createElement("div");
    alert.className = "alert alert-danger mt-3";
    alert.dataset.editorError = "true";
    alert.textContent = message;
    textarea.insertAdjacentElement("beforebegin", alert);
  }

  function ArticleImageUploadAdapter(loader, uploadUrl) {
    this.loader = loader;
    this.uploadUrl = uploadUrl;
    this.xhr = null;
  }

  ArticleImageUploadAdapter.prototype.upload = function () {
    var self = this;
    return this.loader.file.then(function (file) {
      return new Promise(function (resolve, reject) {
        self.initRequest();
        self.initListeners(resolve, reject, file);
        self.sendRequest(file);
      });
    });
  };

  ArticleImageUploadAdapter.prototype.abort = function () {
    if (this.xhr) {
      this.xhr.abort();
    }
  };

  ArticleImageUploadAdapter.prototype.initRequest = function () {
    this.xhr = new XMLHttpRequest();
    this.xhr.open("POST", this.uploadUrl, true);
    this.xhr.responseType = "json";
  };

  ArticleImageUploadAdapter.prototype.initListeners = function (
    resolve,
    reject,
    file,
  ) {
    var xhr = this.xhr;
    var loader = this.loader;
    var genericError = 'Không thể tải ảnh "' + file.name + '" lên lúc này.';

    xhr.addEventListener("error", function () {
      reject(genericError);
    });
    xhr.addEventListener("abort", function () {
      reject();
    });
    xhr.addEventListener("load", function () {
      var response = xhr.response || {};

      if (xhr.status < 200 || xhr.status >= 300) {
        reject(response.message || genericError);
        return;
      }

      if (!response.url) {
        reject("Máy chủ không trả về đường dẫn ảnh hợp lệ.");
        return;
      }

      resolve({ default: response.url });
    });

    if (xhr.upload) {
      xhr.upload.addEventListener("progress", function (event) {
        if (event.lengthComputable) {
          loader.uploadTotal = event.total;
          loader.uploaded = event.loaded;
        }
      });
    }
  };

  ArticleImageUploadAdapter.prototype.sendRequest = function (file) {
    var data = new FormData();
    data.append("file", file);
    this.xhr.send(data);
  };

  function createUploadAdapterPlugin(uploadUrl) {
    return function (editor) {
      var repository = editor.plugins.get("FileRepository");
      repository.createUploadAdapter = function (loader) {
        return new ArticleImageUploadAdapter(loader, uploadUrl);
      };
    };
  }

  function buildEditorConfig(uploadUrl) {
    return {
      extraPlugins: [createUploadAdapterPlugin(uploadUrl)],
      toolbar: [
        "heading",
        "|",
        "bold",
        "italic",
        "link",
        "|",
        "bulletedList",
        "numberedList",
        "blockQuote",
        "|",
        "uploadImage",
        "mediaEmbed",
        "insertTable",
        "|",
        "undo",
        "redo",
      ],
      image: {
        toolbar: [
          "imageStyle:block",
          "imageStyle:side",
          "|",
          "toggleImageCaption",
          "imageTextAlternative",
        ],
      },
      link: {
        addTargetToExternalLinks: true,
      },
    };
  }

  function initEditor(textarea) {
    var uploadUrl = textarea.dataset.uploadUrl;
    if (!uploadUrl) {
      renderEditorBootError(
        textarea,
        "Thiếu cấu hình tải ảnh cho trình soạn thảo bài viết.",
      );
      return;
    }

    if (typeof window.ClassicEditor === "undefined") {
      renderEditorBootError(
        textarea,
        "Không thể tải CKEditor. Vui lòng tải lại trang.",
      );
      return;
    }

    window.ClassicEditor.create(textarea, buildEditorConfig(uploadUrl)).catch(
      function (error) {
        renderEditorBootError(
          textarea,
          "Không thể khởi tạo trình soạn thảo nội dung. Vui lòng tải lại trang hoặc liên hệ quản trị.",
        );
        console.error("Không thể khởi tạo CKEditor.", error);
      },
    );
  }

  function initArticleEditors() {
    var editors = document.querySelectorAll('[data-rich-editor="article"]');
    editors.forEach(function (textarea) {
      initEditor(textarea);
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initArticleEditors, {
      once: true,
    });
  } else {
    initArticleEditors();
  }
})();
