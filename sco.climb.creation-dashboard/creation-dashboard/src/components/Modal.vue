<script>
  export default {
    name: 'modal',
    methods: {
        toggleBodyClass(addRemoveClass, className) {
    const el = document.body;

    if (addRemoveClass === 'addClass') {
      el.classList.add(className);
    } else {
      el.classList.remove(className);
    }
  },
  show() {
    console.log('modal mounted');
  this.toggleBodyClass('addClass', 'modal-show');
    },
        close() {
          this.$emit('close');
          console.log('modal detroyed');
          this.toggleBodyClass('removeClass', 'modal-hide');
      },
},



  };
</script>
<template>
  <transition name="modal-fade">
    <div class="modal-backdrop" v-init="show">
      <div class="modal w-2/3 z-50 m-0 absolute "
        role="dialog"
        aria-labelledby="modalTitle"
        aria-describedby="modalDescription"
      >
        <header
          class="modal-header"
          id="modalTitle"
        >
          <slot name="header">
            Titolo

            <button
              type="button"
              class="btn-close"
              @click="close"
              aria-label="Close modal"
            >
              x
            </button>
          </slot>
        </header>
        <section
          class="modal-body"
          id="modalDescription"
        >
          <slot name="body">
            Body
          </slot>
        </section>
        <div class="modal-footer">
          <slot name="footer">
            Footer

            <button
              type="button"
              class="btn-green"
              @click="close"
              aria-label="Close modal"
            >
              Chiudi
            </button>
          </slot>
        </div>
      </div>
    </div>
  </transition>
</template>
<style>
 .modal-fade-enter,
  .modal-fade-leave-active {
    opacity: 0;
  }

  .modal-fade-enter-active,
  .modal-fade-leave-active {
    transition: opacity .5s ease
  }
  .modal-backdrop {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.3);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
  }

  .modal {
    background: #FFFFFF;
    box-shadow: 2px 2px 20px 1px;
    overflow-x: visible;
    display: flex;
    flex-direction: column;
    /* text-align: center; */
  }

  .modal-header,
  .modal-footer {
    /* padding: 15px; */
    /* display: flex; */
    text-align: center;
    z-index: 9;
  }

  .modal-header {
    border-bottom: 1px solid #eeeeee;
    color: #4AAE9B;
    justify-content: space-between;
    height: 50px;
    line-height: 50px;
        font-size: large;
    font-weight: bold;
  }

  .modal-footer {
    border-top: 1px solid #eeeeee;
    justify-content: flex-end;
  }

  .modal-body {
    position: relative;
    padding: 20px 10px;
    max-height: calc(100vh - 210px);
    min-height: 200px;
    overflow-y: scroll;
    overflow-x: visible;
    padding: 1.25em;
  }

  .btn-close {
    border: none;
    font-size: 20px;
    padding: 20px;
    cursor: pointer;
    font-weight: bold;
    color: #4AAE9B;
    background: transparent;
  }

  .btn-green {
    color: white;
    background: #4AAE9B;
    border: 1px solid #4AAE9B;
    border-radius: 2px;
  }
  
</style>