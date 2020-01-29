package org.mozilla.deepspeech.libdeepspeech;

/**
 * @brief Exposes a DeepSpeech model in Java
 **/
public class DeepSpeechModel {

    static {
        System.loadLibrary("deepspeech-jni");
        System.loadLibrary("deepspeech");
    }

    // FIXME: We should have something better than those SWIGTYPE_*
    SWIGTYPE_p_p_ModelState _mspp;
    SWIGTYPE_p_ModelState   _msp;

   /**
    * @brief An object providing an interface to a trained DeepSpeech model.
    *
    * @constructor
    *
    * @param modelPath The path to the frozen model graph.
    */
    public DeepSpeechModel(String modelPath) {
        this._mspp = impl.new_modelstatep();
        impl.CreateModel(modelPath, this._mspp);
        this._msp  = impl.modelstatep_value(this._mspp);
    }

   /**
    * @brief Get beam width value used by the model. If setModelBeamWidth was not
    *        called before, will return the default value loaded from the model file.
    *
    * @return Beam width value used by the model.
    */
    public int beamWidth() {
        return impl.GetModelBeamWidth(this._msp);
    }

    /**
     * @brief Set beam width value used by the model.
     *
     * @param aBeamWidth The beam width used by the model. A larger beam width value
     *                   generates better results at the cost of decoding time.
     *
     * @return Zero on success, non-zero on failure.
     */
    public int setBeamWidth(int beamWidth) {
        return impl.SetModelBeamWidth(this._msp, beamWidth);
    }

   /**
    * @brief Return the sample rate expected by the model.
    *
    * @return Sample rate.
    */
    public int sampleRate() {
        return impl.GetModelSampleRate(this._msp);
    }

   /**
    * @brief Frees associated resources and destroys model object.
    */
    public void freeModel() {
        impl.FreeModel(this._msp);
    }

   /**
    * @brief Enable decoding using an external scorer.
    *
    * @param scorer The path to the external scorer file.
    *
    * @return Zero on success, non-zero on failure (invalid arguments).
    */
    public void enableExternalScorer(String scorer) {
        impl.EnableExternalScorer(this._msp, scorer);
    }

    /**
    * @brief Disable decoding using an external scorer.
    *
    * @return Zero on success, non-zero on failure (invalid arguments).
    */
    public void disableExternalScorer() {
        impl.DisableExternalScorer(this._msp);
    }

    /**
    * @brief Enable decoding using beam scoring with a KenLM language model.
    *
    * @param alpha The alpha hyperparameter of the decoder. Language model weight.
    * @param beta The beta hyperparameter of the decoder. Word insertion weight.
    *
    * @return Zero on success, non-zero on failure (invalid arguments).
    */
    public void setScorerAlphaBeta(float alpha, float beta) {
        impl.SetScorerAlphaBeta(this._msp, alpha, beta);
    }

   /*
    * @brief Use the DeepSpeech model to perform Speech-To-Text.
    *
    * @param buffer A 16-bit, mono raw audio signal at the appropriate
    *                sample rate (matching what the model was trained on).
    * @param buffer_size The number of samples in the audio signal.
    *
    * @return The STT result.
    */
    public String stt(short[] buffer, int buffer_size) {
        return impl.SpeechToText(this._msp, buffer, buffer_size);
    }

   /**
    * @brief Use the DeepSpeech model to perform Speech-To-Text and output metadata
    * about the results.
    *
    * @param buffer A 16-bit, mono raw audio signal at the appropriate
    *                sample rate (matching what the model was trained on).
    * @param buffer_size The number of samples in the audio signal.
    *
    * @return Outputs a Metadata object of individual letters along with their timing information.
    */
    public Metadata sttWithMetadata(short[] buffer, int buffer_size) {
        return impl.SpeechToTextWithMetadata(this._msp, buffer, buffer_size);
    }

   /**
    * @brief Create a new streaming inference state. The streaming state returned
    *        by this function can then be passed to feedAudioContent()
    *        and finishStream().
    *
    * @return An opaque object that represents the streaming state.
    */
    public DeepSpeechStreamingState createStream() {
        SWIGTYPE_p_p_StreamingState ssp = impl.new_streamingstatep();
        impl.CreateStream(this._msp, ssp);
        return new DeepSpeechStreamingState(impl.streamingstatep_value(ssp));
    }

   /**
    * @brief Feed audio samples to an ongoing streaming inference.
    *
    * @param cctx A streaming state pointer returned by createStream().
    * @param buffer An array of 16-bit, mono raw audio samples at the
    *                appropriate sample rate (matching what the model was trained on).
    * @param buffer_size The number of samples in @p buffer.
    */
    public void feedAudioContent(DeepSpeechStreamingState ctx, short[] buffer, int buffer_size) {
        impl.FeedAudioContent(ctx.get(), buffer, buffer_size);
    }

   /**
    * @brief Compute the intermediate decoding of an ongoing streaming inference.
    *
    * @param ctx A streaming state pointer returned by createStream().
    *
    * @return The STT intermediate result.
    */
    public String intermediateDecode(DeepSpeechStreamingState ctx) {
        return impl.IntermediateDecode(ctx.get());
    }

   /**
    * @brief Signal the end of an audio signal to an ongoing streaming
    *        inference, returns the STT result over the whole audio signal.
    *
    * @param ctx A streaming state pointer returned by createStream().
    *
    * @return The STT result.
    *
    * @note This method will free the state pointer (@p ctx).
    */
    public String finishStream(DeepSpeechStreamingState ctx) {
        return impl.FinishStream(ctx.get());
    }

   /**
    * @brief Signal the end of an audio signal to an ongoing streaming
    *        inference, returns per-letter metadata.
    *
    * @param ctx A streaming state pointer returned by createStream().
    *
    * @return Outputs a Metadata object of individual letters along with their timing information.
    *
    * @note This method will free the state pointer (@p ctx).
    */
    public Metadata finishStreamWithMetadata(DeepSpeechStreamingState ctx) {
        return impl.FinishStreamWithMetadata(ctx.get());
    }
}
