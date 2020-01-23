#ifndef __ARGS_H__
#define __ARGS_H__

#if defined(_MSC_VER)
#include "getopt_win.h"
#else
#include <getopt.h>
#endif
#include <iostream>

#include "deepspeech.h"

char* model = NULL;

char* scorer = NULL;

char* audio = NULL;

int beam_width = 500;

bool set_alphabeta = false;

float lm_alpha = 0.f;

float lm_beta = 0.f;

bool show_times = false;

bool has_versions = false;

bool extended_metadata = false;

bool json_output = false;

int stream_size = 0;

void PrintHelp(const char* bin)
{
    std::cout <<
    "Usage: " << bin << " --model MODEL [--scorer SCORER] --audio AUDIO [-t] [-e]\n"
    "\n"
    "Running DeepSpeech inference.\n"
    "\n"
    "\t--model MODEL\t\tPath to the model (protocol buffer binary file)\n"
    "\t--scorer SCORER\t\tPath to the external scorer file\n"
    "\t--audio AUDIO\t\tPath to the audio file to run (WAV format)\n"
    "\t--beam_width BEAM_WIDTH\tValue for decoder beam width (int)\n"
    "\t--lm_alpha LM_ALPHA\tValue for language model alpha param (float)\n"
    "\t--lm_beta LM_BETA\tValue for language model beta param (float)\n"
    "\t-t\t\t\tRun in benchmark mode, output mfcc & inference time\n"
    "\t--extended\t\tOutput string from extended metadata\n"
    "\t--json\t\t\tExtended output, shows word timings as JSON\n"
    "\t--stream size\t\tRun in stream mode, output intermediate results\n"
    "\t--help\t\t\tShow help\n"
    "\t--version\t\tPrint version and exits\n";
    DS_PrintVersions();
    exit(1);
}

bool ProcessArgs(int argc, char** argv)
{
    const char* const short_opts = "m:l:a:b:c:d:tejs:vh";
    const option long_opts[] = {
            {"model", required_argument, nullptr, 'm'},
            {"scorer", required_argument, nullptr, 'l'},
            {"audio", required_argument, nullptr, 'a'},
            {"beam_width", required_argument, nullptr, 'b'},
            {"lm_alpha", required_argument, nullptr, 'c'},
            {"lm_beta", required_argument, nullptr, 'd'},
            {"t", no_argument, nullptr, 't'},
            {"extended", no_argument, nullptr, 'e'},
            {"json", no_argument, nullptr, 'j'},
            {"stream", required_argument, nullptr, 's'},
            {"version", no_argument, nullptr, 'v'},
            {"help", no_argument, nullptr, 'h'},
            {nullptr, no_argument, nullptr, 0}
    };

    while (true)
    {
        const auto opt = getopt_long(argc, argv, short_opts, long_opts, nullptr);

        if (-1 == opt)
            break;

        switch (opt)
        {
        case 'm':
            model = optarg;
            break;

        case 'l':
            scorer = optarg;
            break;

        case 'a':
            audio = optarg;
            break;

        case 'b':
            beam_width = atoi(optarg);
            break;

        case 'c':
            set_alphabeta = true;
            lm_alpha = atof(optarg);
            break;

        case 'd':
            set_alphabeta = true;
            lm_beta = atof(optarg);
            break;

        case 't':
            show_times = true;
            break;

        case 'e':
            extended_metadata = true;
            break;

        case 'j':
            json_output = true;
            break;

        case 's':
            stream_size = atoi(optarg);
            break;

        case 'v':
            has_versions = true;
            break;

        case 'h': // -h or --help
        case '?': // Unrecognized option
        default:
            PrintHelp(argv[0]);
            break;
        }
    }

    if (has_versions) {
        DS_PrintVersions();
        return false;
    }

    if (!model || !audio) {
        PrintHelp(argv[0]);
        return false;
    }

    if (stream_size < 0 || stream_size % 160 != 0) {
        std::cout <<
        "Stream buffer size must be multiples of 160\n";
        return false;
    }

    return true;
}

#endif // __ARGS_H__
